package org.itsallcode.whiterabbit.jfxui.table;

import java.time.Duration;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.util.StringConverter;

class DurationStringConverter extends StringConverter<Duration>
{
    private final FormatterService formatter;

    DurationStringConverter(FormatterService formatter)
    {
        this.formatter = formatter;
    }

    @Override
    public String toString(Duration duration)
    {
        return duration != null ? formatter.format(duration) : null;
    }

    @Override
    public Duration fromString(String string)
    {
        if (string.isBlank())
        {
            return Duration.ZERO;
        }
        final LocalTime parsed = LocalTime.parse(string);

        return Duration.ofHours(parsed.getHour()).plusMinutes(parsed.getMinute());
    }
}