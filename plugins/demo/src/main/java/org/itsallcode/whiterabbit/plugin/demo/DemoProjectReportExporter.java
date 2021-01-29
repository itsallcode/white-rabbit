package org.itsallcode.whiterabbit.plugin.demo;

import java.time.Duration;

import org.itsallcode.whiterabbit.api.ProgressMonitor;
import org.itsallcode.whiterabbit.api.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.model.IProjectReport;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;

public class DemoProjectReportExporter implements ProjectReportExporter
{
    @SuppressWarnings("unused")
    @Override
    public void export(IProjectReport report, ProgressMonitor progressMonitor)
    {
        progressMonitor.setTaskName("Initializing...");
        if (progressMonitor.isCanceled())
        {
            return;
        }

        sleep(Duration.ofSeconds(2));

        progressMonitor.beginTask("Initializing...", report.getDays().size());
        for (final Day day : report.getDays())
        {
            if (progressMonitor.isCanceled())
            {
                return;
            }
            progressMonitor.worked(1);
            progressMonitor.setTaskName("Exporting day " + day.getDate() + "...");

            sleep(Duration.ofMillis(200));
            for (final ProjectActivity project : day.getProjects())
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
