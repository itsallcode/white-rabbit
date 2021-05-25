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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + offsetInDays;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        final EasterBasedHoliday other = (EasterBasedHoliday) obj;
        if (offsetInDays != other.offsetInDays)
        {
            return false;
        }
        return true;
    }

}
