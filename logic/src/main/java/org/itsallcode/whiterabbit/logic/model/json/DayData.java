package org.itsallcode.whiterabbit.logic.model.json;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.itsallcode.whiterabbit.api.model.DayType;

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

    List<JsonActivity> getActivities();

    void setActivities(List<JsonActivity> activities);

}