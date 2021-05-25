package org.itsallcode.whiterabbit.logic.holidays;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Manages a list of holidays typically defined in a configuration file. Each
 * holiday is meant to repeat every year and may have
 * 
 * <li>a fixed date identical for every year
 * <li>a floating date defined by a specific date in each year and an offset
 * restricted to a particular day of the week, e.g. fourth Sunday before
 * Christmas
 * <li>a date defined relatively to Easter Sunday with a positive or negative
 * offset of days
 */
public class Holidays
{
    List<Holiday> definitions = new ArrayList<>();

    public boolean add(Holiday holiday)
    {
        return definitions.add(holiday);
    }

    /**
     * Applies the list of holidays to given month in given year and returns the
     * set of days of given month that are holidays.
     */
    public Set<Integer> getDayNumbers(int year, int month)
    {
        return getInstances(year, month).stream()
                .map(HolidayInstance::getDayOfMonth)
                .collect(toSet());
    }

    public List<HolidayInstance> getInstances(int year, int month)
    {
        return definitions.stream()
                .map(h -> h.getInstance(year))
                .filter(h -> h.isIn(month))
                .sorted(HolidayInstance::compareTo)
                .collect(toList());
    }

    public List<Holiday> getDefinitions()
    {
        return definitions;
    }

}
