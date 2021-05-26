package org.itsallcode.whiterabbit.logic.holidays;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Hosts a personal set of holidays typically defined in a configuration file.
 * Each holiday is meant to repeat every year and may have
 * 
 * <li>a fixed date identical for every year
 * <li>a floating date defined by a specific date in each year and an offset
 * restricted to a particular day of the week, e.g. fourth Sunday before
 * Christmas
 * <li>a date defined relatively to Easter Sunday with a positive or negative
 * offset of days
 */
public class HolidayService
{
    final List<Holiday> definitions = new ArrayList<>();
    // caches
    Set<Integer> years = new HashSet<>();
    Hashtable<LocalDate, List<Holiday>> holidayInstances = new Hashtable<>();

    public HolidayService(final List<Holiday> list)
    {
        definitions.addAll(list);
    }

    /**
     * @return List of holidays occurring on the given date. If there is no
     *         holiday on given date, then list is empty.
     */
    public List<Holiday> getHolidays(LocalDate date)
    {
        cacheHolidays(date.getYear());

        final List<Holiday> instances = holidayInstances.get(date);
        if (instances == null)
        {
            return Collections.<Holiday> emptyList();
        }
        return instances;
    }

    private void cacheHolidays(final int year)
    {
        if (years.contains(year))
        {
            return;
        }

        for (final Holiday holiday : definitions)
        {
            final LocalDate date = holiday.of(year);
            List<Holiday> entry = holidayInstances.get(date);
            if (entry == null)
            {
                entry = new ArrayList<>();
                holidayInstances.put(date, entry);
            }
            entry.add(holiday);
        }
        years.add(year);
    }

    List<Holiday> getDefinitions()
    {
        return definitions;
    }

}
