package org.itsallcode.whiterabbit.api.model;

/**
 * The type of a {@link DayData day}.
 */
public enum DayType
{
    /** Public holiday (not working) */
    HOLIDAY(false),
    /** Vacation, paid time off (not working) */
    VACATION(false),
    /** Flex time, reduce overtime (not working) */
    FLEX_TIME(true),
    /** Sick, paid sick leave (not working) */
    SICK(false),
    /** Normal working day */
    WORK(true),
    /** Weekend (Saturday, Sunday, not working) */
    WEEKEND(false);

    private final boolean workDay;

    private DayType(final boolean workDay)
    {
        this.workDay = workDay;
    }

    /**
     * Check if this is a working day.
     * 
     * @return <code>true</code> if you need to work on a day of this type.
     */
    public boolean isWorkDay()
    {
        return workDay;
    }
}
