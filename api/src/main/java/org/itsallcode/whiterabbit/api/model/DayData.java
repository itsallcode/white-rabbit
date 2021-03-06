package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Model class representing a day with date, time for begin and end of work,
 * activities etc.
 */
public interface DayData
{
    LocalDate getDate();

    DayType getType();

    LocalTime getBegin();

    LocalTime getEnd();

    Duration getInterruption();

    Duration getWorkingHours();

    String getComment();

    void setDate(LocalDate date);

    void setType(DayType type);

    void setBegin(LocalTime begin);

    void setEnd(LocalTime end);

    void setWorkingHours(Duration workingHours);

    void setComment(String comment);

    void setInterruption(Duration interruption);

    List<ActivityData> getActivities();

    void setActivities(List<ActivityData> activities);
}