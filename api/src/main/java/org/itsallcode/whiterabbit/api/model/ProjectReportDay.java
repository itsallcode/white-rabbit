package org.itsallcode.whiterabbit.api.model;

import java.time.LocalDate;
import java.util.List;

public interface ProjectReportDay
{
    LocalDate getDate();

    DayType getType();

    String getComment();

    List<ProjectReportActivity> getProjects();
}