package org.itsallcode.whiterabbit.logic.holidays;

import java.time.LocalDate;

public class FixedDateHoliday extends Holiday
{
    private final int month;
    private final int day;

    public FixedDateHoliday(String name, int month, int day)
    {
        super(name);
        this.month = month;
        this.day = day;
    }

    @Override
    public LocalDate of(int year)
    {
        return LocalDate.of(year, month, day);
    }
}
