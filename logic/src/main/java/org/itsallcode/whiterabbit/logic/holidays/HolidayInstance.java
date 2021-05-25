package org.itsallcode.whiterabbit.logic.holidays;

import java.time.LocalDate;

public class HolidayInstance
{
    private final LocalDate date;
    private final Holiday definition;

    public HolidayInstance(int year, Holiday definition)
    {
        this.definition = definition;
        this.date = definition.of(year);
    }

    public boolean isIn(int month)
    {
        return date.getMonthValue() == month;
    }

    public int compareTo(HolidayInstance other)
    {
        return date.compareTo(other.date);
    }

    public int getDayOfMonth()
    {
        return date.getDayOfMonth();
    }

    public LocalDate getDate()
    {
        return date;
    }

    public Holiday getDefinition()
    {
        return definition;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((definition == null) ? 0 : definition.hashCode());
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
        final HolidayInstance other = (HolidayInstance) obj;
        if (date == null)
        {
            if (other.date != null)
            {
                return false;
            }
        }
        else if (!date.equals(other.date))
        {
            return false;
        }
        if (definition == null)
        {
            if (other.definition != null)
            {
                return false;
            }
        }
        else if (!definition.equals(other.definition))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("%s: %s)", definition.getName(), date);
    }
}
