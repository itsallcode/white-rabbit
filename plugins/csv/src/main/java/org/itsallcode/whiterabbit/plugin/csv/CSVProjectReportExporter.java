package org.itsallcode.whiterabbit.plugin.csv;

import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;

class CSVProjectReportExporter implements ProjectReportExporter
{
    private final boolean filterForWeekDays;
    private final OutStreamProvider outStreamProvider;
    private final String separator;

    CSVProjectReportExporter(boolean filterForWeekDays, String separator, OutStreamProvider outStreamProvider) {
        this.filterForWeekDays = filterForWeekDays;
        this.outStreamProvider = outStreamProvider;
        this.separator = separator;
    }

    @Override
    public void export(ProjectReport report, ProgressMonitor progressMonitor)
    {
        progressMonitor.setTaskName("Exporting...");
        if (progressMonitor.isCanceled())
        {
            return;
        }
        List<ProjectReportDay> filteredDays = filterForWeekDays ?
                report.getDays().stream().filter(day -> day.getType() == DayType.WORK).collect(Collectors.toList()) :
                report.getDays();

        progressMonitor.beginTask("Initializing...", report.getDays().size());
        try {
            PrintStream os = new PrintStream(outStreamProvider.getStream(report.getMonth().toString()));

            os.println(MessageFormat.format("Date{0}Project{0}Time{0}Comment", separator));

            for (final ProjectReportDay day : filteredDays)
            {
                if (progressMonitor.isCanceled())
                {
                    return;
                }
                progressMonitor.worked(1);
                final String dayComment = formatEmptyString(day.getComment());
                os.println(MessageFormat.format("{0}{1}{1}{1}{2}", day.getDate().toString(), separator, dayComment));

                for (final ProjectReportActivity project : day.getProjects())
                {
                    progressMonitor.setTaskName(getTaskName(day, project));
                    if (progressMonitor.isCanceled())
                    {
                        return;
                    }
                    final String duration = formatDuration(project.getWorkingTime());
                    final String projectLabel = formatEmptyString(project.getProject().getLabel());
                    final String activityComment = formatEmptyString(project.getComment());
                    os.println(MessageFormat.format("{0}{1}{0}{2}{0}{3}",
                            separator, projectLabel, duration, activityComment));
                }
            }
            os.close();

        } catch (IOException ex) {
            throw new RuntimeException("Error exporting report to CSV:" + ex.toString());
        }
    }

    private String formatEmptyString(String value) {
        return value == null ? "" : value;
    }

    private String getTaskName(final ProjectReportDay day, ProjectReportActivity project)
    {
        if (project == null)
        {
            return "Exporting " + day.getDate() + "...";
        }
        return "Exporting " + day.getDate() + " / " + project.getProject().getLabel() + "...";
    }

    public static String formatDuration(Duration duration) {
        long minutes = TimeUnit.SECONDS.toMinutes(duration.getSeconds());
        long absMinutes = Math.abs(minutes);
        String positive = String.format("%02d:%02d", absMinutes / 60, absMinutes % 60);
        return minutes < 0 ? "-" + positive : positive;
    }
}
