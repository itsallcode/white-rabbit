package org.itsallcode.whiterabbit.logic.service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.json.DayType;

public class FormatterService
{
    private static final int MAX_DAY_TYPE_LENGTH = getMaxDayTypeLength();

    private final Locale locale;

    private final ZoneId timeZoneId;

    public FormatterService()
    {
        this(Locale.UK, ZoneId.systemDefault());
    }

    private FormatterService(Locale locale, ZoneId timeZoneId)
    {
        this.locale = locale;
        this.timeZoneId = timeZoneId;
    }

    public String format(DayRecord day)
    {
        final String dayOfWeek = day.getDate().getDayOfWeek()
                .getDisplayName(TextStyle.SHORT_STANDALONE, locale);
        final String dayType = formatDayType(day.getType());
        final String date = format("{0} {1} {2}", day.getDate(), dayOfWeek, dayType);
        final String time = day.getBegin() != null
                ? format("{0} - {1}", day.getBegin(), day.getEnd())
                : "             ";
        final String interruption = day.getInterruption().isZero() ? ""
                : "interr.: " + format(day.getInterruption()) + ", ";
        return format("{0} {1} break: {2}, {3}working time: {4}, overtime: {5}, total: {6}", date,
                time, format(day.getMandatoryBreak()), interruption, format(day.getWorkingTime()),
                format(day.getOvertime()), format(day.getOverallOvertime()));
    }

    private String formatDayType(DayType type)
    {
        final String formatPattern = "%1$-" + MAX_DAY_TYPE_LENGTH + "s";
        return String.format(locale, formatPattern, type);
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

    public String formatDateAndtime(Instant instant)
    {
        final LocalDateTime dateTime = LocalDateTime.ofInstant(instant, timeZoneId);
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).withLocale(locale)
                .withZone(timeZoneId);
        return dateTime.format(dateTimeFormatter);
    }
}
