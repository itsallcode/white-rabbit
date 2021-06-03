package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.holidays.calculator.logic.Holiday;
import org.itsallcode.holidays.calculator.logic.HolidayService;
import org.itsallcode.holidays.calculator.logic.parser.HolidaysFileParser;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;

class CalculatedHolidays implements Holidays
{
    private static final Logger LOG = LogManager.getLogger(CalculatedHolidays.class);
    private static final String HOLIDAYS_CONFIGURATION_FILE = "holidays.cfg";

    private final PluginConfiguration config;
    private final HolidayService service;

    public CalculatedHolidays(PluginConfiguration config)
    {
        this.config = config;
        service = new HolidayService(readHolidays());
    }

    CalculatedHolidays(PluginConfiguration config, String inputSourceIdentifier, InputStream stream)
    {
        this.config = config;
        service = new HolidayService(readHolidays(null, inputSourceIdentifier, stream));
    }

    private List<Holiday> readHolidays()
    {
        final Path file = config.getDataDir().resolve(HOLIDAYS_CONFIGURATION_FILE);
        if (!Files.exists(file))
        {
            LOG.warn("Could not find holiday definitions file {}. Using empty list of holidays.",
                    file);
            return new ArrayList<>();
        }
        LOG.info("Reading holiday definitions from {}", file);
        return readHolidays(file, null, null);
    }

    private List<Holiday> readHolidays(Path configurationFile, String inputSourceIdentifier, InputStream stream)
    {
        String message = "";
        if (configurationFile != null)
        {
            inputSourceIdentifier = configurationFile.toString();
        }
        message = "Error reading holiday definitions from " + message + inputSourceIdentifier;

        try (InputStream stream2 = (stream == null ? Files.newInputStream(configurationFile) : stream))
        {
            return new HolidaysFileParser(inputSourceIdentifier).parse(stream2);
        }
        catch (final IOException e)
        {
            LOG.error(message, e);
            throw new UncheckedIOException(message, e);
        }
    }

    @Override
    public List<DayData> getHolidays(ModelFactory factory, YearMonth month)
    {
        return new HolidayAggregator(factory, service).getHolidays(month);
    }

}
