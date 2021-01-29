package org.itsallcode.whiterabbit.logic.report.project;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;

public class ProjectReportImpl implements ProjectReport
{
    private final YearMonth month;
    private final List<ProjectReportDay> days;

    ProjectReportImpl(YearMonth month, List<ProjectReportDay> days)
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
    public List<ProjectReportDay> getDays()
    {
        return days;
    }

    public static class DayImpl implements ProjectReportDay
    {
        private final LocalDate date;
        private final DayType type;
        private final String comment;
        private final List<ProjectReportActivity> projects;

        DayImpl(LocalDate date, DayType type, String comment, List<ProjectReportActivity> projects)
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
        public List<ProjectReportActivity> getProjects()
        {
            return projects;
        }
    }

    public static class ProjectActivityImpl implements ProjectReportActivity
    {
        private final ProjectImpl project;
        private final Duration workingTime;
        private final String comment;

        public ProjectActivityImpl(ProjectImpl project, Duration workingTime, String comment)
        {
            this.project = project;
            this.workingTime = workingTime;
            this.comment = comment;
        }

        @Override
        public ProjectImpl getProject()
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
