package org.itsallcode.whiterabbit.api.model;

/**
 * The type of a {@link DayData day}.
 */
public enum DayType
{
    HOLIDAY(false), VACATION(false), FLEX_TIME(true), SICK(false), WORK(true), WEEKEND(false);

    private final boolean workDay;

    DayType(boolean workDay)
    {
        this.workDay = workDay;
    }

    /**
     * @return <code>true</code> if you need to work on a day of this type.
     */
    public boolean isWorkDay()
    {
        return workDay;
    }
}
