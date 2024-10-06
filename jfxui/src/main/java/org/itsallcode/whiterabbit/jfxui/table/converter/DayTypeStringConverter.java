package org.itsallcode.whiterabbit.jfxui.table.converter;

import org.itsallcode.whiterabbit.api.model.DayType;

import javafx.util.StringConverter;

public class DayTypeStringConverter extends StringConverter<DayType>
{
    @Override
    public String toString(final DayType object)
    {
        return object != null ? object.name() : null;
    }

    @Override
    public DayType fromString(final String string)
    {
        return DayType.valueOf(string);
    }
}
