package org.itsallcode.whiterabbit.api.model;

import java.time.YearMonth;
import java.util.List;

/**
 * A monthly project report.
 */
public interface ProjectReport
{
    YearMonth getMonth();

    List<ProjectReportDay> getDays();

    List<ProjectReportActivity> getProjects();
}