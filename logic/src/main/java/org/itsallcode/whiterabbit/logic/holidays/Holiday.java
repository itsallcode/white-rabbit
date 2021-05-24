package org.itsallcode.whiterabbit.logic.holidays;

import java.time.LocalDate;

public abstract class Holiday
{
    public abstract LocalDate of(int year);

    private final String name;

    public Holiday(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

}
