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
import org.itsallcode.holidays.calculator.logic.HolidaySet;
import org.itsallcode.holidays.calculator.logic.parser.HolidaysFileParser;
import org.itsallcode.holidays.calculator.logic.variants.Holiday;
import org.itsallcode.whiterabbit.api.features.Holidays;

class CalculatedHolidays implements org.itsallcode.whiterabbit.api.features.Holidays
{
    private static final Logger LOG = LogManager.getLogger(CalculatedHolidays.class);
    public static final String HOLIDAYS_CONFIGURATION_FILE = "holidays.cfg";

    private final HolidaySet holidaySet;

    public CalculatedHolidays(Path dataDir)
    {
        holidaySet = new HolidaySet(readHolidays(dataDir.resolve(HOLIDAYS_CONFIGURATION_FILE)));
    }

    protected List<Holiday> readHolidays(Path configurationFile)
    {
        if (!Files.exists(configurationFile))
        {
            LOG.warn("Could not find holiday definitions file '{}'. Using empty list of holidays.", configurationFile);
            // In this place CalculatedHolidays could notify a callback in order
            // to display a warning in GUI.
            return new ArrayList<>();
        }
        LOG.info("Reading holiday definitions from {}", configurationFile);

        final String filename = configurationFile.toString();
        final String message = "Error reading holiday definitions from " + filename;

        try (InputStream stream = Files.newInputStream(configurationFile))
        {
            final HolidaysFileParser parser = new HolidaysFileParser(filename);
            // Maybe evaluate parser.getError() and feed potential result back
            // to GUI callback?
            return parser.parse(stream);
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
        return holidaySet.instances(date).stream()
                .map(h -> new HolidayInstanceImpl(h.getCategory(), h.getName(), date))
                .collect(toList());
    }

}
