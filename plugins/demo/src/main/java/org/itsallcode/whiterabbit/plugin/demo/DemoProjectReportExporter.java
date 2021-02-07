package org.itsallcode.whiterabbit.plugin.demo;

import java.time.Duration;

import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;

class DemoProjectReportExporter implements ProjectReportExporter
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

        progressMonitor.beginTask("Initializing...", report.getDays().size());
        for (final ProjectReportDay day : report.getDays())
        {
            if (progressMonitor.isCanceled())
            {
                return;
            }
            progressMonitor.worked(1);
            progressMonitor.setTaskName(getTaskName(day, null));

            sleep(Duration.ofMillis(200));
            for (final ProjectReportActivity project : day.getProjects())
            {
                progressMonitor.setTaskName(getTaskName(day, project));
                if (progressMonitor.isCanceled())
                {
                    return;
                }
                sleep(Duration.ofMillis(200));
            }
        }
    }

    private String getTaskName(final ProjectReportDay day, ProjectReportActivity project)
    {
        if (project == null)
        {
            return "Exporting " + day.getDate() + "...";
        }
        return "Exporting " + day.getDate() + " / " + project.getProject().getLabel() + "...";
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
