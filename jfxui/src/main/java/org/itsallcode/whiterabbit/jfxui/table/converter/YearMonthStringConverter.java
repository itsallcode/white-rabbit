package org.itsallcode.whiterabbit.jfxui.table.converter;

import java.time.YearMonth;

import javafx.util.StringConverter;

public class YearMonthStringConverter extends StringConverter<YearMonth>
{
    @Override
    public String toString(YearMonth yearMonth)
    {
        return yearMonth.toString();
    }

    @Override
    public YearMonth fromString(String string)
    {
        return YearMonth.parse(string);
    }
}
