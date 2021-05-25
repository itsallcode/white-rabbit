package org.itsallcode.whiterabbit.logic.holidays.parser;

import java.time.DayOfWeek;
import java.util.Hashtable;

public class DayOfWeekParser
{
    Hashtable<String, DayOfWeek> cache = new Hashtable<>();

    public DayOfWeek getDayOfWeek(final String prefix)
    {
        if (cache.contains(prefix))
        {
            return cache.get(prefix);
        }

        String upper = "";
        if (prefix != null)
        {
            upper = prefix.toUpperCase();
        }

        for (final DayOfWeek day : DayOfWeek.values())
        {
            if (day.toString().toUpperCase().startsWith(upper))
            {
                cache.put(prefix, day);
                return day;
            }
        }
        return null;
    }

}
