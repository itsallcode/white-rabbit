package org.itsallcode.whiterabbit.jfxui.table.converter;

import java.time.Year;

import javafx.util.StringConverter;

public class YearStringConverter extends StringConverter<Year>
{
    @Override
    public String toString(Year year)
    {
        return year.toString();
    }

    @Override
    public Year fromString(String string)
    {
        return Year.parse(string);
    }
}
