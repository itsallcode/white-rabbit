package org.itsallcode.whiterabbit.logic.service.holidays;

import java.time.LocalDate;

import org.itsallcode.whiterabbit.api.features.Holidays.HolidayInstance;

public class HolidayInstanceImpl implements HolidayInstance
{
    private final String name;
    private final LocalDate date;

    public HolidayInstanceImpl(String name, LocalDate date)
    {
        this.name = name;
        this.date = date;
    }

    @Override
    public String getCategory()
    {
        return null;
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
