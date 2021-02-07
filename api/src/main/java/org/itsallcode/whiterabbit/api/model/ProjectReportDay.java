package org.itsallcode.whiterabbit.api.model;

import java.time.LocalDate;
import java.util.List;

/**
 * The projects worked on during a single day of the {@link ProjectReport}
 * including date and {@link ProjectReportActivity activities}.
 */
public interface ProjectReportDay
{
    LocalDate getDate();

    DayType getType();

    String getComment();

    List<ProjectReportActivity> getProjects();
}