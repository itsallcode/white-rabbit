package org.itsallcode.whiterabbit.logic.service.holidays;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;

import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.Holidays.HolidayInstance;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.DayType;

class HolidayAggregator
{
    // used to separate names of of multiple holidays occurring on the same day
    private static final String DELIMITER = ",";
    private final HashMap<LocalDate, List<HolidayInstance>> holidays = new HashMap<>();

    public void collect(Holidays holidayProvider, YearMonth month)
    {
        for (int d = 1; d <= month.atEndOfMonth().getDayOfMonth(); d++)
        {
            collect(holidayProvider, month.atDay(d));
        }
    }

    public List<DayData> createDayData(ModelFactory factory)
    {
        return holidays.entrySet().stream()
                .map(e -> toDayData(factory, e.getKey(), e.getValue()))
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(toList());
    }

    private void collect(Holidays holidayProvider, LocalDate date)
    {
        final List<HolidayInstance> instances = holidayProvider.getHolidays(date);
        if (instances.isEmpty())
        {
            return;
        }
        holidays.put(date, instances);
    }

    private DayData toDayData(ModelFactory factory, LocalDate date, List<HolidayInstance> instances)
    {
        final DayData holiday = factory.createDayData();
        holiday.setDate(date);
        // Depending on the category of each instance we could set different
        // types here.
        holiday.setType(DayType.HOLIDAY);
        final String comment = instances.stream().map(HolidayInstance::getName).collect(joining(DELIMITER));
        holiday.setComment(comment);
        return holiday;
    }
}
