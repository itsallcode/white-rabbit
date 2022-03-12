package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import java.time.LocalDate;

import org.itsallcode.whiterabbit.api.features.Holidays.HolidayInstance;

record HolidayInstanceImpl(String category, String name, LocalDate date) implements HolidayInstance
{
    @Override
    public String getCategory()
    {
        return category;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public LocalDate getDate()
    {
        return date;
    }
}
