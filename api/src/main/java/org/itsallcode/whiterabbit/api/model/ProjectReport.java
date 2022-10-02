package org.itsallcode.whiterabbit.api.model;

import java.time.YearMonth;
import java.util.List;

/**
 * A monthly project report.
 */
public interface ProjectReport
{
    /**
     * Get the year and month of this project report.
     * 
     * @return the year and month of this project report.
     */
    YearMonth getMonth();

    /**
     * Get the days in this project report.
     * 
     * @return the days in this project report.
     */
    List<ProjectReportDay> getDays();

    /**
     * Get the activities in this project report.
     * 
     * @return the activities in this project report.
     */
    List<ProjectReportActivity> getProjects();
}