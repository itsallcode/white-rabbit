package org.itsallcode.whiterabbit.logic.report.project;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportImpl.DayImpl;
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
        return new ProjectReportImpl(month, storage.loadMonth(month)
                .map(MonthIndex::getSortedDays).orElse(Stream.empty())
                .map(this::generateDayReport)
                .collect(toList()));
    }

    private ProjectReportDay generateDayReport(DayRecord record)
    {
        final List<ProjectReportActivity> projects = record.activities()
                .getAll().stream()
                .filter(activity -> activity.getProject() != null)
                .collect(groupingBy(this::activityProject))
                .values().stream()
                .map(this::aggregateProject)
                .collect(toList());

        return new DayImpl(record.getDate(), record.getType(), record.getComment(), projects);
    }

    private String activityProject(Activity activity)
    {
        return activity.getProject().getProjectId();
    }

    private ProjectReportImpl.ProjectActivityImpl aggregateProject(List<Activity> projectActivites)
    {
        final Duration totalWorkingTime = projectActivites.stream()
                .filter(activity -> activity.getDuration() != null)
                .map(Activity::getDuration).reduce((a, b) -> a.plus(b))
                .orElse(Duration.ZERO);
        final String comments = projectActivites.stream()
                .map(Activity::getComment)
                .filter(Objects::nonNull)
                .filter(comment -> !comment.isBlank())
                .collect(joining(", "));
        return new ProjectReportImpl.ProjectActivityImpl(projectActivites.get(0).getProject(), totalWorkingTime, comments);
    }
}
