package org.itsallcode.whiterabbit.logic.storage.data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.itsallcode.whiterabbit.api.model.ActivityData;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.DayType;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "date", "type", "begin", "end", "interruption", "workingHours", "comment", "activities" })
public class JsonDay implements DayData
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
    private List<ActivityData> activities;

    @Override
    public LocalDate getDate()
    {
        return date;
    }

    @Override
    public DayType getType()
    {
        return type;
    }

    @Override
    public LocalTime getBegin()
    {
        return begin;
    }

    @Override
    public LocalTime getEnd()
    {
        return end;
    }

    @Override
    public Duration getInterruption()
    {
        return interruption;
    }

    @Override
    public Duration getWorkingHours()
    {
        return workingHours;
    }

    @Override
    public String getComment()
    {
        return comment;
    }

    @Override
    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    @Override
    public void setType(DayType type)
    {
        this.type = type;
    }

    @Override
    public void setBegin(LocalTime begin)
    {
        this.begin = begin;
    }

    @Override
    public void setEnd(LocalTime end)
    {
        this.end = end;
    }

    @Override
    public void setWorkingHours(Duration workingHours)
    {
        this.workingHours = workingHours;
    }

    @Override
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public void setInterruption(Duration interruption)
    {
        this.interruption = interruption;
    }

    @Override
    public List<ActivityData> getActivities()
    {
        return activities;
    }

    @Override
    public void setActivities(List<ActivityData> activities)
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
