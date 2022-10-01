package org.itsallcode.whiterabbit.api.model;

import java.time.LocalDate;
import java.util.List;

/**
 * The projects worked on during a single day of the {@link ProjectReport}
 * including date and {@link ProjectReportActivity activities}.
 */
public interface ProjectReportDay
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
     * Get the comment of this day.
     * 
     * @return the comment of this day.
     */
    String getComment();

    /**
     * Get the project activities of this day.
     * 
     * @return the project activities of this day.
     */
    List<ProjectReportActivity> getProjects();
}