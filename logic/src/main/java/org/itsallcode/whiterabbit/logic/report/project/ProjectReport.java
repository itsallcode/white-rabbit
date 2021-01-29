package org.itsallcode.whiterabbit.logic.report.project;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.itsallcode.whiterabbit.api.model.IProjectReport;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.service.project.Project;

public class ProjectReport implements IProjectReport
{
    private final YearMonth month;
    private final List<Day> days;

    ProjectReport(YearMonth month, List<Day> days)
    {
        this.month = month;
        this.days = days;
    }

    @Override
    public YearMonth getMonth()
    {
        return month;
    }

    @Override
    public List<Day> getDays()
    {
        return days;
    }

    public static class Day
    {
        private final LocalDate date;
        private final DayType type;
        private final String comment;
        private final List<ProjectActivity> projects;

        Day(LocalDate date, DayType type, String comment, List<ProjectActivity> projects)
        {
            this.date = date;
            this.type = type;
            this.comment = comment;
            this.projects = projects;
        }

        public LocalDate getDate()
        {
            return date;
        }

        public DayType getType()
        {
            return type;
        }

        public String getComment()
        {
            return comment;
        }

        public List<ProjectActivity> getProjects()
        {
            return projects;
        }
    }

    public static class ProjectActivity
    {
        private final Project project;
        private final Duration workingTime;
        private final String comment;

        public ProjectActivity(Project project, Duration workingTime, String comment)
        {
            this.project = project;
            this.workingTime = workingTime;
            this.comment = comment;
        }

        public Project getProject()
        {
            return project;
        }

        public Duration getWorkingTime()
        {
            return workingTime;
        }

        public String getComment()
        {
            return comment;
        }
    }
}
