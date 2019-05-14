package org.itsallcode.whiterabbit.jfxui.ui;

import org.itsallcode.whiterabbit.logic.model.json.DayType;

import javafx.util.StringConverter;

class DayTypeStringConverter extends StringConverter<DayType>
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