package org.itsallcode.whiterabbit.logic.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DayOfWeekWithoutDotFormatter
{
    DateTimeFormatter dateTimeFormatter;

    public DayOfWeekWithoutDotFormatter(DateTimeFormatter formatter)
    {
        this.dateTimeFormatter = formatter;
    }

    public String format(LocalDateTime dateTime)
    {
        return dayOfWeekWithoutDot(dateTimeFormatter.format(dateTime));
    }

    public String format(LocalDate date)
    {
        return dayOfWeekWithoutDot(dateTimeFormatter.format(date));
    }

    public String dayOfWeekWithoutDot(String str)
    {
        if (str == null || str.length() < 3)
        {
            return str;
        }

        if ('.' == str.charAt(2))
        {
            str = str.substring(0, 2) + str.substring(3);
        }
        return str;
    }

}
