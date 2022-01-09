package org.itsallcode.whiterabbit.api.model;

import java.time.YearMonth;
import java.util.List;

/**
 * A monthly project report.
 */
public interface ProjectReport
{
    /**
     * @return the year and month of this project report.
     */
    YearMonth getMonth();

    /**
     * @return the days in this project report.
     */
    List<ProjectReportDay> getDays();

    /**
     * @return the activities in this project report.
     */
    List<ProjectReportActivity> getProjects();
}