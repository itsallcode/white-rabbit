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
        ensureValidDate(month, day);
    }

    @Override
    public LocalDate of(int year)
    {
        return LocalDate.of(year, month, day);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + day;
        result = prime * result + month;
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
        final FixedDateHoliday other = (FixedDateHoliday) obj;
        if (day != other.day)
        {
            return false;
        }
        if (month != other.month)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s: %02d-%02d)", this.getClass().getSimpleName(), getName(), month, day);
    }
}
