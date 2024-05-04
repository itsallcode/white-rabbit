package org.itsallcode.whiterabbit.logic.report.project;

import static java.util.stream.Collectors.groupingBy;

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

    public ProjectReportGenerator(final Storage storage)
    {
        this.storage = storage;
    }

    public ProjectReport generateReport(final YearMonth month)
    {
        final List<DayRecord> sortedDays = storage.loadMonth(month)
                .map(MonthIndex::getSortedDays).orElse(Stream.empty()).toList();

        final List<ProjectReportDay> reportDays = sortedDays.stream()
                .map(this::generateDayReport)
                .toList();

        final List<ProjectReportActivity> reportProjects = sortedDays.stream()
                .flatMap(day -> day.activities().getAll().stream())
                .filter(activity -> activity.getProject() != null)
                .collect(groupingBy(Activity::getProject))
                .values().stream()
                .map(ProjectReportGenerator::aggregateProject)
                .toList();

        return new ProjectReportImpl(month, reportDays, reportProjects);
    }

    private ProjectReportDay generateDayReport(final DayRecord dayRecord)
    {
        final List<ProjectReportActivity> projects = dayRecord.activities()
                .getAll().stream()
                .filter(activity -> activity.getProject() != null)
                .collect(groupingBy(ProjectReportGenerator::activityProject))
                .values().stream()
                .map(ProjectReportGenerator::aggregateProject)
                .toList();

        return new DayImpl(dayRecord.getDate(), dayRecord.getType(), dayRecord.getComment(), projects);
    }

    private static String activityProject(final Activity activity)
    {
        return activity.getProject().getProjectId();
    }

    private static ProjectReportActivity aggregateProject(final List<Activity> projectActivites)
    {
        final Duration totalWorkingTime = projectActivites.stream()
                .filter(activity -> activity.getDuration() != null)
                .map(Activity::getDuration).reduce(Duration::plus)
                .orElse(Duration.ZERO);
        final List<String> comments = projectActivites.stream()
                .map(Activity::getComment)
                .filter(Objects::nonNull)
                .filter(comment -> !comment.isBlank())
                .distinct()
                .toList();
        return new ProjectReportImpl.ProjectActivityImpl(projectActivites.get(0).getProject(), totalWorkingTime,
                comments);
    }
}
