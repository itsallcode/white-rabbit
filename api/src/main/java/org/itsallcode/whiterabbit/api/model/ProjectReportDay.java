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
     * @return the date of this day.
     */
    LocalDate getDate();

    /**
     * @return the {@link DayType} of this day.
     */
    DayType getType();

    /**
     * @return the comment of this day.
     */
    String getComment();

    /**
     * @return the project activities of this day.
     */
    List<ProjectReportActivity> getProjects();
}