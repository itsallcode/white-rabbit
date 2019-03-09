package org.itsallcode.whiterabbit.logic.model.json;

public enum DayType {
    HOLIDAY(false), VACATION(false), FLEX_TIME(true), SICK(false), WORK(true), WEEKEND(false);

    private final boolean workDay;

    private DayType(boolean workDay) {
	this.workDay = workDay;
    }

    public boolean isWorkDay() {
	return workDay;
    }
}
