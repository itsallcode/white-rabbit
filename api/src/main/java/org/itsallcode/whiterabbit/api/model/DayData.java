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
    /**
     * Get the date of this day.
     * 
     * @return the date of this day.
     */
    LocalDate getDate();

    /**
     * Get the {@link DayType} of this day.
     * 
     * @return the {@link DayType} of this day.
     */
    DayType getType();

    /**
     * Get the begin working time of this day.
     * 
     * @return the begin working time of this day.
     */
    LocalTime getBegin();

    /**
     * Get the end working time of this day.
     * 
     * @return the end working time of this day.
     */
    LocalTime getEnd();

    /**
     * Get the duration of the interruption of this day.
     * 
     * @return the duration of the interruption of this day.
     */
    Duration getInterruption();

    /**
     * Get the working hours of this day.
     * 
     * @return the working hours of this day.
     */
    Duration getWorkingHours();

    /**
     * Get the comment of this day.
     * 
     * @return the comment of this day.
     */
    String getComment();

    /**
     * Set the date for this day.
     * 
     * @param date
     *            the new date.
     */
    void setDate(LocalDate date);

    /**
     * Set the {@link DayType} for this day.
     * 
     * @param type
     *            the new {@link DayType}.
     */
    void setType(DayType type);

    /**
     * Set the begin time for this day.
     * 
     * @param begin
     *            the new begin time.
     */
    void setBegin(LocalTime begin);

    /**
     * Set the end time for this day.
     * 
     * @param end
     *            the new end time.
     */
    void setEnd(LocalTime end);

    /**
     * Set the working hours for this day.
     * 
     * @param workingHours
     *            the new working hours.
     */
    void setWorkingHours(Duration workingHours);

    /**
     * Set the comment for this day.
     * 
     * @param comment
     *            the new comment.
     */
    void setComment(String comment);

    /**
     * Set the duration of the interruption for this day.
     * 
     * @param interruption
     *            the new duration of the interruption.
     */
    void setInterruption(Duration interruption);

    /**
     * Get the the {@link ActivityData}s for this day.
     * 
     * @return the {@link ActivityData}s for this day.
     */
    List<ActivityData> getActivities();

    /**
     * Set the {@link ActivityData}s for this day.
     * 
     * @param activities
     *            new new activities.
     */
    void setActivities(List<ActivityData> activities);
}
