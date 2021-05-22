package org.itsallcode.whiterabbit.logic.service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatterService
{
    private final Locale locale;
    private final ZoneId timeZoneId;
    private final DayOfWeekWithoutDotFormatter dateTimeFormatter;

    public FormatterService(Locale locale, ZoneId timeZoneId)
    {
        this.locale = locale;
        this.timeZoneId = timeZoneId;
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE dd.MM.yyyy 'CW'ww, HH:mm:ss")
                .withZone(timeZoneId)
                .withLocale(locale);
        this.dateTimeFormatter = new DayOfWeekWithoutDotFormatter(formatter);
    }

    public String format(Duration duration)
    {
        final String sign = duration.isNegative() ? "-" : "";
        final long hours = Math.abs(duration.toHours());
        final int minutes = Math.abs(duration.toMinutesPart());
        return format("{0}{1,number,00}:{2,number,00}", sign, hours, minutes);
    }

    private String format(String pattern, final Object... arguments)
    {
        final MessageFormat temp = new MessageFormat(pattern, locale);
        return temp.format(arguments);
    }

    public String formatDateAndTime(Instant instant)
    {
        final LocalDateTime dateTime = LocalDateTime.ofInstant(instant, timeZoneId);
        return dateTimeFormatter.format(dateTime);
    }

    public Locale getLocale()
    {
        return locale;
    }

    public DayOfWeekWithoutDotFormatter getCustomShortDateFormatter()
    {
        return new DayOfWeekWithoutDotFormatter(DateTimeFormatter.ofPattern("E dd.MM.", locale));
    }

    public DateTimeFormatter getShortDateFormatter()
    {
        return DateTimeFormatter.ofPattern("E dd.MM.", locale);
    }
}
