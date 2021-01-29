package org.itsallcode.whiterabbit.api.model;

import java.time.YearMonth;
import java.util.List;

import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;

public interface IProjectReport
{
    YearMonth getMonth();

    List<Day> getDays();
}