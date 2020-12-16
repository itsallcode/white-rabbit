package org.itsallcode.whiterabbit.logic.report.project;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class ProjectReportGenerator
{
    private final Storage storage;

    public ProjectReportGenerator(Storage storage)
    {
        this.storage = storage;
    }

    public ProjectReport generateReport(YearMonth month)
    {
        return new ProjectReport(storage.loadMonth(month)
                .map(MonthIndex::getSortedDays).orElse(Stream.empty())
                .map(this::generateDayReport)
                .collect(toList()));
    }

    private ProjectReport.Day generateDayReport(DayRecord record)
    {
        final List<ProjectActivity> projects = record.activities()
                .getAll().stream()
                .filter(activity -> activity.getProject() != null)
                .collect(groupingBy(this::activityProject))
                .values().stream()
                .map(this::aggregateProject)
                .collect(toList());

        return new Day(record.getDate(), record.getType(), projects);
    }

    private String activityProject(Activity activity)
    {
        return activity.getProject().getProjectId();
    }

    private ProjectReport.ProjectActivity aggregateProject(List<Activity> projectActivites)
    {
        final Duration totalWorkingTime = projectActivites.stream()
                .filter(activity -> activity.getDuration() != null)
                .map(Activity::getDuration).reduce((a, b) -> a.plus(b))
                .orElse(Duration.ZERO);
        return new ProjectReport.ProjectActivity(projectActivites.get(0).getProject(), totalWorkingTime);
    }
}
