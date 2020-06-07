package org.itsallcode.whiterabbit.logic.model;

import org.itsallcode.whiterabbit.logic.model.json.JsonActivity;

public class Activity
{
    private final JsonActivity jsonActivity;
    private final DayRecord day;

    public Activity(JsonActivity jsonActivity, DayRecord day)
    {
        this.jsonActivity = jsonActivity;
        this.day = day;
    }

    public DayRecord getDay()
    {
        return day;
    }

    public void updateValuesFrom(Activity activity)
    {
        this.jsonActivity.setComment(activity.jsonActivity.getComment());
        this.jsonActivity.setProjectId(activity.jsonActivity.getProjectId());
        this.jsonActivity.setDuration(activity.jsonActivity.getDuration());
    }
}
