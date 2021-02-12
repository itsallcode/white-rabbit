package org.itsallcode.whiterabbit.logic.service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Locale;

import org.itsallcode.whiterabbit.api.model.DayType;

public class FormatterService
{
    private static final int MAX_DAY_TYPE_LENGTH = getMaxDayTypeLength();

    private final Locale locale;
    private final ZoneId timeZoneId;
    private final DateTimeFormatter dateTimeFormatter;

    public FormatterService(Locale locale, ZoneId timeZoneId)
    {
        this.locale = locale;
        this.timeZoneId = timeZoneId;
        this.dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)
                .withLocale(locale).withZone(timeZoneId);
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

    private static int getMaxDayTypeLength()
    {
        return Arrays.stream(DayType.values()) //
                .map(DayType::toString) //
                .mapToInt(String::length) //
                .max().getAsInt();
    }

    public String formatDateAndTime(Instant instant)
    {
        final LocalDateTime dateTime = LocalDateTime.ofInstant(instant, timeZoneId);
        return dateTime.format(dateTimeFormatter);
    }
}
