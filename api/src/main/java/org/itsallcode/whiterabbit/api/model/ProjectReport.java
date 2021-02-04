package org.itsallcode.whiterabbit.api.model;

import java.time.YearMonth;
import java.util.List;

public interface ProjectReport
{
    YearMonth getMonth();

    List<ProjectReportDay> getDays();
}