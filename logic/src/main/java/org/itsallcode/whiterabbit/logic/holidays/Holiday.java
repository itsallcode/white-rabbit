package org.itsallcode.whiterabbit.logic.holidays;

import java.time.LocalDate;

public abstract class Holiday
{
    private static final int PIVOT_YEAR = 2000;

    public abstract LocalDate of(int year);

    private final String category;
    private final String name;

    /**
     * @param category
     *            Arbitrary category that may be evaluated by the application
     *            processing the holiday.
     * @param name
     */
    public Holiday(String category, String name)
    {
        this.category = category;
        this.name = name;
    }

    public String getCategory()
    {
        return category;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Ensure date can be valid, at least in a leap year
     */
    protected void ensureValidDate(int month, int day)
    {
        LocalDate.of(PIVOT_YEAR, month, day);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        final Holiday other = (Holiday) obj;
        if (category == null)
        {
            if (other.category != null)
            {
                return false;
            }
        }
        else if (!category.equals(other.category))
        {
            return false;
        }
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        return true;
    }

}
