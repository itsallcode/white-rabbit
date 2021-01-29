package org.itsallcode.whiterabbit.logic.report.project;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.IProjectReport;
import org.itsallcode.whiterabbit.api.model.IProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.IProjectReportDay;
import org.itsallcode.whiterabbit.logic.service.project.Project;

public class ProjectReport implements IProjectReport
{
    private final YearMonth month;
    private final List<IProjectReportDay> days;

    ProjectReport(YearMonth month, List<IProjectReportDay> days)
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
    public List<IProjectReportDay> getDays()
    {
        return days;
    }

    public static class Day implements IProjectReportDay
    {
        private final LocalDate date;
        private final DayType type;
        private final String comment;
        private final List<IProjectReportActivity> projects;

        Day(LocalDate date, DayType type, String comment, List<IProjectReportActivity> projects)
        {
            this.date = date;
            this.type = type;
            this.comment = comment;
            this.projects = projects;
        }

        @Override
        public LocalDate getDate()
        {
            return date;
        }

        @Override
        public DayType getType()
        {
            return type;
        }

        @Override
        public String getComment()
        {
            return comment;
        }

        @Override
        public List<IProjectReportActivity> getProjects()
        {
            return projects;
        }
    }

    public static class ProjectActivity implements IProjectReportActivity
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

        @Override
        public Project getProject()
        {
            return project;
        }

        @Override
        public Duration getWorkingTime()
        {
            return workingTime;
        }

        @Override
        public String getComment()
        {
            return comment;
        }
    }
}
