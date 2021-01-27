package org.itsallcode.whiterabbit.plugin.demo;

import java.time.Duration;

import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;
import org.itsallcode.whiterabbit.plugin.ProgressMonitor;
import org.itsallcode.whiterabbit.plugin.ProjectReportExporter;

public class DemoProjectReportExporter implements ProjectReportExporter
{
    @Override
    public void export(ProjectReport report, ProgressMonitor progressMonitor)
    {
        progressMonitor.setTaskName("Initializing...");
        if (progressMonitor.isCanceled())
        {
            return;
        }

        sleep(Duration.ofSeconds(2));

        progressMonitor.beginTask("Initializing...", report.days.size());
        for (final Day day : report.days)
        {
            if (progressMonitor.isCanceled())
            {
                return;
            }
            progressMonitor.worked(1);
            progressMonitor.setTaskName("Exporting day " + day.date + "...");

            sleep(Duration.ofMillis(200));
            for (@SuppressWarnings("unused")
            final ProjectActivity project : day.projects)
            {
                if (progressMonitor.isCanceled())
                {
                    return;
                }
                sleep(Duration.ofMillis(200));
            }
        }
    }

    private void sleep(Duration duration)
    {
        try
        {
            Thread.sleep(duration.toMillis());
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
