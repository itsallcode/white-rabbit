package org.itsallcode.whiterabbit.api.model;

import java.time.YearMonth;
import java.util.List;

public interface IProjectReport
{
    YearMonth getMonth();

    List<IProjectReportDay> getDays();
}