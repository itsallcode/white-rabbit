package org.itsallcode.whiterabbit.logic.model;

import java.time.Duration;

import org.itsallcode.whiterabbit.logic.model.json.JsonActivity;

public class Activity
{
    private final JsonActivity jsonActivity;
    private final DayActivities day;

    public Activity(JsonActivity jsonActivity, DayActivities day)
    {
        this.jsonActivity = jsonActivity;
        this.day = day;
    }

    public DayRecord getDay()
    {
        return day.dayRecord;
    }

    public void updateValuesFrom(Activity activity)
    {
        this.jsonActivity.setComment(activity.jsonActivity.getComment());
        this.jsonActivity.setProjectId(activity.jsonActivity.getProjectId());
        this.jsonActivity.setDuration(activity.jsonActivity.getDuration());
    }

    public String getProjectId()
    {
        return jsonActivity.getProjectId();
    }

    public void setProjectId(String id)
    {
        this.jsonActivity.setProjectId(id);
    }

    public Duration getDuration()
    {
        return jsonActivity.getDuration();
    }

    public void setDuration(Duration duration)
    {
        jsonActivity.setDuration(duration);
    }

    public String getComment()
    {
        return jsonActivity.getComment();
    }

    public void setComment(String comment)
    {
        jsonActivity.setComment(comment);
    }
}
