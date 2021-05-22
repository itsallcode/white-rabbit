package org.itsallcode.whiterabbit.jfxui.table.days;

import java.time.LocalDate;

import org.itsallcode.whiterabbit.logic.service.DayOfWeekWithoutDotFormatter;

import javafx.util.converter.LocalDateStringConverter;

public class CustomLocalDateStringConverter extends LocalDateStringConverter
{
    DayOfWeekWithoutDotFormatter dateTimeFormatter;

    public CustomLocalDateStringConverter(DayOfWeekWithoutDotFormatter dateTimeFormatter)
    {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String toString(LocalDate value)
    {
        return value == null ? null : dateTimeFormatter.format(value.atStartOfDay());
    }

}
