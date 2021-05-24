package org.itsallcode.whiterabbit.logic.holidays;

import java.time.LocalDate;

public class EasterBasedHoliday extends Holiday
{
    private final int offsetInDays;

    public EasterBasedHoliday(String name, int offsetInDays)
    {
        super(name);
        this.offsetInDays = offsetInDays;
    }

    @Override
    public LocalDate of(int year)
    {
        return GaussEasterCalculator.calculate(year).plusDays(offsetInDays);
    }

}
