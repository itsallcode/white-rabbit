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
    private final List<ProjectReportActivity> projects;

    public ProjectReportImpl(YearMonth month, List<ProjectReportDay> days, List<ProjectReportActivity> projects)
    {
        this.month = month;
        this.days = days;
        this.projects = projects;
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

    @Override
    public List<ProjectReportActivity> getProjects()
    {
        return projects;
    }

    public static class DayImpl implements ProjectReportDay
    {
        private final LocalDate date;
        private final DayType type;
        private final String comment;
        private final List<ProjectReportActivity> projects;

        public DayImpl(LocalDate date, DayType type, String comment, List<ProjectReportActivity> projects)
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
        private final List<String> comments;

        public ProjectActivityImpl(ProjectImpl project, Duration workingTime, List<String> comments)
        {
            this.project = project;
            this.workingTime = workingTime;
            this.comments = comments;
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
        public List<String> getComments()
        {
            return comments;
        }
    }
}
