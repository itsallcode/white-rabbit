package org.itsallcode.whiterabbit.logic.service.holidays;

import static java.util.stream.Collectors.joining;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
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
    private static final String DELIMITER = ", ";
    private final HashMap<LocalDate, List<HolidayInstance>> holidays = new HashMap<>();
    private final ModelFactory modelFactory;

    HolidayAggregator(ModelFactory modelFactory)
    {
        this.modelFactory = modelFactory;
    }

    public void collect(Holidays holidayProvider, YearMonth month)
    {
        for (int d = 1; d <= month.atEndOfMonth().getDayOfMonth(); d++)
        {
            collect(holidayProvider, month.atDay(d));
        }
    }

    public List<DayData> createDayData()
    {
        return holidays.entrySet().stream()
                .map(e -> toDayData(e.getKey(), e.getValue()))
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .toList();
    }

    private void collect(Holidays holidayProvider, LocalDate date)
    {
        final List<HolidayInstance> instances = holidayProvider.getHolidays(date);
        if (instances.isEmpty())
        {
            return;
        }

        final List<HolidayInstance> entry = holidays.get(date);
        if (entry != null)
        {
            entry.addAll(instances);
        }
        else
        {
            holidays.put(date, new ArrayList<>(instances));
        }
    }

    private DayData toDayData(LocalDate date, List<HolidayInstance> instances)
    {
        final DayData holiday = modelFactory.createDayData();
        holiday.setDate(date);
        // Depending on the category of each instance we could set different
        // types here.
        holiday.setType(DayType.HOLIDAY);
        final String comment = instances.stream().map(HolidayInstance::getName).collect(joining(DELIMITER));
        holiday.setComment(comment);
        return holiday;
    }
}
