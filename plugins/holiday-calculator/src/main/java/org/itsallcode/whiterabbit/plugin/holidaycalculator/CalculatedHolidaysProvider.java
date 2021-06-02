package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.holidays.calculator.logic.Holiday;
import org.itsallcode.holidays.calculator.logic.HolidayService;
import org.itsallcode.holidays.calculator.logic.parser.HolidaysFileParser;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.HolidayProvider;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.DayType;

class CalculatedHolidaysProvider implements HolidayProvider
{
    private static final Logger LOG = LogManager.getLogger(CalculatedHolidaysProvider.class);
    private static final String HOLIDAYS_CONFIGURATION_FILE = "holidays.cfg";

    private final PluginConfiguration config;
    private final HolidayService service;

    public CalculatedHolidaysProvider(PluginConfiguration config)
    {
        this.config = config;
        service = new HolidayService(readHolidays());
    }

    private List<Holiday> readHolidays()
    {
        final Path userDir = config.getUserDir();
        final String configurationFile = config.getMandatoryValue(HOLIDAYS_CONFIGURATION_FILE);

        final Path path = userDir.resolve(configurationFile);
        try (InputStream stream = Files.newInputStream(path))
        {
            return new HolidaysFileParser(configurationFile).parse(stream);
        }
        catch (final IOException e)
        {
            LOG.error("Error reading configuration file {}", path, e);
            throw new UncheckedIOException("Error reading configuration file " + path, e);
        }
    }

    @Override
    public List<DayData> getHolidays(ModelFactory factory, YearMonth month)
    {
        final List<DayData> holidays = new ArrayList<>();
        for (int d = 1; d <= month.atEndOfMonth().getDayOfMonth(); d++)
        {
            final DayData dayData = aggregate(factory, month.atDay(d));
            if (dayData != null)
            {
                holidays.add(dayData);
            }
        }
        return holidays;
    }

    private DayData aggregate(ModelFactory factory, LocalDate date)
    {
        final List<Holiday> holidays = service.getHolidays(date);
        if (holidays.isEmpty())
        {
            return null;
        }

        final DayData holiday = factory.createDayData();
        holiday.setDate(date);
        holiday.setType(DayType.HOLIDAY);
        final String comment = holidays.stream().map(Holiday::getName).collect(joining(","));
        holiday.setComment(comment);
        return holiday;
    }

}
