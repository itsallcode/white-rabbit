package org.itsallcode.whiterabbit.logic.model.json;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.itsallcode.whiterabbit.api.model.DayType;

@JsonbPropertyOrder({ "date", "type", "begin", "end", "interruption", "workingHours", "comment", "activities" })
public class JsonDay
{
    @JsonbProperty("date")
    private LocalDate date;
    @JsonbProperty("type")
    private DayType type;
    @JsonbProperty("begin")
    private LocalTime begin;
    @JsonbProperty("end")
    private LocalTime end;
    @JsonbProperty("interruption")
    private Duration interruption;
    @JsonbProperty("workingHours")
    private Duration workingHours;
    @JsonbProperty("comment")
    private String comment;
    @JsonbProperty("activities")
    private List<JsonActivity> activities;

    public LocalDate getDate()
    {
        return date;
    }

    public DayType getType()
    {
        return type;
    }

    public LocalTime getBegin()
    {
        return begin;
    }

    public LocalTime getEnd()
    {
        return end;
    }

    public Duration getInterruption()
    {
        return interruption;
    }

    public Duration getWorkingHours()
    {
        return workingHours;
    }

    public String getComment()
    {
        return comment;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    public void setType(DayType type)
    {
        this.type = type;
    }

    public void setBegin(LocalTime begin)
    {
        this.begin = begin;
    }

    public void setEnd(LocalTime end)
    {
        this.end = end;
    }

    public void setWorkingHours(Duration workingHours)
    {
        this.workingHours = workingHours;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public void setInterruption(Duration interruption)
    {
        this.interruption = interruption;
    }

    public List<JsonActivity> getActivities()
    {
        return activities;
    }

    public void setActivities(List<JsonActivity> activities)
    {
        this.activities = activities;
    }

    @Override
    public String toString()
    {
        return "JsonDay [date=" + date + ", type=" + type + ", begin=" + begin + ", end=" + end + ", interruption="
                + interruption + ", workingHours=" + workingHours + ", comment=" + comment + ", activities="
                + activities + "]";
    }
}
