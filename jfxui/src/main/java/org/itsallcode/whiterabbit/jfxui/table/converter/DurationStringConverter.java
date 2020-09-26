package org.itsallcode.whiterabbit.jfxui.table.converter;

import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.util.StringConverter;

public class DurationStringConverter extends StringConverter<Duration>
{
    private static final Pattern DURATION_PATTERN = Pattern.compile("(?:(?<hours>\\d+):)?(?<minutes>\\d+)");
    private final FormatterService formatter;

    public DurationStringConverter(FormatterService formatter)
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
        if (string.trim().isBlank())
        {
            return Duration.ZERO;
        }
        final Matcher matcher = DURATION_PATTERN.matcher(string.trim());
        if (!matcher.matches())
        {
            return null;
        }

        final int hours = Optional.ofNullable(matcher.group("hours")).map(Integer::parseInt).orElse(0);
        final int minutes = Integer.parseInt(matcher.group("minutes"));
        return Duration.ofHours(hours).plusMinutes(minutes);
    }
}
