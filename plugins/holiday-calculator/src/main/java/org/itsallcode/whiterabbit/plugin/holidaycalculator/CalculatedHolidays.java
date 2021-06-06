package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.holidays.calculator.logic.Holiday;
import org.itsallcode.holidays.calculator.logic.HolidayService;
import org.itsallcode.holidays.calculator.logic.parser.HolidaysFileParser;
import org.itsallcode.whiterabbit.api.features.Holidays;

class CalculatedHolidays implements org.itsallcode.whiterabbit.api.features.Holidays
{
    private static final Logger LOG = LogManager.getLogger(CalculatedHolidays.class);
    public static final String HOLIDAYS_CONFIGURATION_FILE = "holidays.cfg";

    private final HolidayService holidaySet;

    public CalculatedHolidays(Path dataDir)
    {
        holidaySet = new HolidayService(readHolidays(dataDir.resolve(HOLIDAYS_CONFIGURATION_FILE)));
    }

    CalculatedHolidays(Path dataDir, String inputSourceIdentifier, InputStream stream)
    {
        holidaySet = new HolidayService(readHolidays(null, inputSourceIdentifier, stream));
    }

    private List<Holiday> readHolidays(Path configurationFile)
    {
        if (!Files.exists(configurationFile))
        {
            final String message = "Could not find holiday definitions file " + configurationFile
                    + ". Using empty list of holidays.";
            LOG.warn(message);
            // In this place CalculatedHolidays could notify a callback in order
            // to display a warning in GUI.
            return new ArrayList<>();
        }
        LOG.info("Reading holiday definitions from {}", configurationFile);
        return readHolidays(configurationFile, null, null);
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
            final HolidaysFileParser parser = new HolidaysFileParser(inputSourceIdentifier);
            // Maybe evaluate parser.getError() and feed potential result back
            // to GUI callback?
            return parser.parse(stream2);
        }
        catch (final IOException e)
        {
            LOG.error(message, e);
            throw new UncheckedIOException(message, e);
        }
    }

    @Override
    public List<Holidays.HolidayInstance> getHolidays(LocalDate date)
    {
        return holidaySet.getHolidays(date).stream()
                .map(h -> new HolidayInstanceImpl(h.getCategory(), h.getName(), date))
                .collect(toList());
    }

}
