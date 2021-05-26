package org.itsallcode.whiterabbit.logic.holidays.parser;

import java.time.DayOfWeek;
import java.util.Hashtable;

public class DayOfWeekParser
{
    public static class AmbigueDayOfWeekAbbreviationException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        public AmbigueDayOfWeekAbbreviationException(String message)
        {
            super(message);
        }
    }

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

        DayOfWeek result = null;
        for (final DayOfWeek day : DayOfWeek.values())
        {
            if (day.toString().toUpperCase().startsWith(upper))
            {
                if (result != null)
                {
                    throw new AmbigueDayOfWeekAbbreviationException(prefix);
                }
                result = day;
            }
        }

        if (result != null)
        {
            cache.put(prefix, result);
        }

        return result;
    }

}
