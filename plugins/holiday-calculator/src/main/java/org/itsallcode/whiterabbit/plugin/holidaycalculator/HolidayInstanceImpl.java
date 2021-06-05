package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import java.time.LocalDate;

import org.itsallcode.whiterabbit.api.features.Holidays.HolidayInstance;

public class HolidayInstanceImpl implements HolidayInstance
{
    private final String category;
    private final String name;
    private final LocalDate date;

    public HolidayInstanceImpl(String category, String name, LocalDate date)
    {
        this.category = category;
        this.name = name;
        this.date = date;
    }

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
