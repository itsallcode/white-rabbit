package org.itsallcode.whiterabbit.jfxui.ui;

import java.time.Duration;

import javafx.util.StringConverter;

class DurationStringConverter extends StringConverter<Duration>
{
    @Override
    public String toString(Duration duration)
    {
        return duration != null ? duration.toString() : null;
    }

    @Override
    public Duration fromString(String string)
    {
        return Duration.parse(string);
    }
}