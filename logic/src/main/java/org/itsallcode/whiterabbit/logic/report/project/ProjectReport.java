package org.itsallcode.whiterabbit.logic.report.project;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.service.project.Project;

public class ProjectReport
{
    public final List<Day> days;

    ProjectReport(List<Day> days)
    {
        this.days = days;
    }

    public static class Day
    {
        public final LocalDate date;
        public final DayType type;
        public final List<ProjectActivity> projects;

        Day(LocalDate date, DayType type, List<ProjectActivity> projects)
        {
            this.date = date;
            this.type = type;
            this.projects = projects;
        }
    }

    public static class ProjectActivity
    {
        public final Project project;
        public final Duration workingTime;

        public ProjectActivity(Project project, Duration workingTime)
        {
            this.project = project;
            this.workingTime = workingTime;
        }
    }
}
