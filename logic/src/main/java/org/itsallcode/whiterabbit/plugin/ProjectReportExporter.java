package org.itsallcode.whiterabbit.plugin;

import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;

public interface ProjectReportExporter
{
    void export(ProjectReport report, ProgressMonitor progressMonitor);
}
