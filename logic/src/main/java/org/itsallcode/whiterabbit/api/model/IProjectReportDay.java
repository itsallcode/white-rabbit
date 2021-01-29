package org.itsallcode.whiterabbit.api.model;

import java.time.LocalDate;
import java.util.List;

import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;

public interface IProjectReportDay
{

    LocalDate getDate();

    DayType getType();

    String getComment();

    List<ProjectActivity> getProjects();

}