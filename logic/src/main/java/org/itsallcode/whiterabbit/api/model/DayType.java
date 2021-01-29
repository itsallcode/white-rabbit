package org.itsallcode.whiterabbit.api.model;

public enum DayType
{
    HOLIDAY(false), VACATION(false), FLEX_TIME(true), SICK(false), WORK(true), WEEKEND(false);

    private final boolean workDay;

    DayType(boolean workDay)
    {
        this.workDay = workDay;
    }

    public boolean isWorkDay()
    {
        return workDay;
    }
}
