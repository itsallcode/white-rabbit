package org.itsallcode.whiterabbit.plugin.csv;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
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
    private final OutStreamProvider outStreamProvider;
    private final CSVConfig csvConfig;

    CSVProjectReportExporter(CSVConfig csvConfig, OutStreamProvider outStreamProvider)
    {
        this.csvConfig = csvConfig;
        this.outStreamProvider = outStreamProvider;
    }

    @Override
    public void export(ProjectReport report, ProgressMonitor progressMonitor)
    {
        progressMonitor.setTaskName("Exporting...");
        if (progressMonitor.isCanceled())
        {
            return;
        }
        final String separator = csvConfig.getSeparator();

        final List<ProjectReportDay> filteredDays = report.getDays().stream()
                .filter(Objects::nonNull)
                .filter(day -> !csvConfig.getFilterForWeekDays() || day.getType() == DayType.WORK)
                .collect(Collectors.toList());

        try (final PrintStream os = new PrintStream(outStreamProvider.getStream(report.getMonth().toString())))
        {
            os.println(MessageFormat.format("Date{0}Project{0}TimePerProject{0}TimePerDay{0}Comment", separator));

            for (final ProjectReportDay day : filteredDays)
            {
                final String dayComment = formatEmptyString(day.getComment());
                final Duration dayWorkingTime = day.getProjects() == null ? Duration.ZERO : getDayWorkingTime(day);
                final String dayDurationStr = formatDuration(dayWorkingTime);

                os.println(MessageFormat.format("{0}{1}{1}{1}{2}{1}{3}",
                        day.getDate(), separator, dayDurationStr, dayComment));

                if (day.getProjects() != null)
                {
                    for (final ProjectReportActivity project : day.getProjects())
                    {
                        final String projectWorkingTime = formatDuration(project.getWorkingTime());
                        final String projectLabel = formatEmptyString(project.getProject().getLabel());
                        final String comments = project.getComments().stream().collect(Collectors.joining(", "));
                        final String activityComment = formatEmptyString(comments);
                        os.println(MessageFormat.format("{0}{1}{0}{2}{0}{0}{3}",
                                separator, projectLabel, projectWorkingTime, activityComment));
                    }
                }
            }
        }
        catch (final IOException ex)
        {
            throw new UncheckedIOException("Error exporting report to CSV:" + ex, ex);
        }
    }

    private Duration getDayWorkingTime(ProjectReportDay day)
    {
        return day.getProjects().stream().reduce(Duration.ZERO,
                (subtotal, element) -> element == null ? subtotal : element.getWorkingTime().plus(subtotal),
                Duration::plus);
    }

    private String formatEmptyString(String value)
    {
        return value == null ? "" : value;
    }

    private String formatDuration(Duration duration)
    {
        final long minutes = TimeUnit.SECONDS.toMinutes(duration.getSeconds());
        final long absMinutes = Math.abs(minutes);
        final String positive = String.format("%02d:%02d", absMinutes / 60, absMinutes % 60);
        return minutes < 0 ? "-" + positive : positive;
    }
}
