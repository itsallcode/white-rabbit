package org.itsallcode.whiterabbit.jfxui.table.converter;

import org.itsallcode.whiterabbit.api.model.DayType;

import javafx.util.StringConverter;

public class DayTypeStringConverter extends StringConverter<DayType>
{
    @Override
    public String toString(DayType object)
    {
        return object != null ? object.name() : null;
    }

    @Override
    public DayType fromString(String string)
    {
        return DayType.valueOf(string);
    }
}