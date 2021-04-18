package org.itsallcode.whiterabbit.jfxui.table.converter;

import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;

public class CustomLocalTimeStringConverter extends StringConverter<LocalTime>
{
    private static final Pattern SIMPLE_DATE_PATTERN = Pattern.compile("(?<hours>\\d{1,2})(?:(?<minutes>\\d{2}))?");
    private final LocalTimeStringConverter delegate;

    public CustomLocalTimeStringConverter(Locale locale)
    {
        delegate = new LocalTimeStringConverter(FormatStyle.SHORT, locale);
    }

    @Override
    public String toString(LocalTime object)
    {
        return delegate.toString(object);
    }

    @Override
    public LocalTime fromString(String string)
    {
        final Matcher matcher = SIMPLE_DATE_PATTERN.matcher(string.trim());
        if (matcher.matches())
        {
            final int hours = Integer.parseInt(matcher.group("hours"));
            final int minutes = Optional.ofNullable(matcher.group("minutes")).map(Integer::parseInt).orElse(0);
            return LocalTime.of(hours, minutes);
        }
        return delegate.fromString(string);
    }
}
