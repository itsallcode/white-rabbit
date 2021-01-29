package org.itsallcode.whiterabbit.api;

import org.itsallcode.whiterabbit.api.model.ProjectReport;

public interface ProjectReportExporter
{
    void export(ProjectReport report, ProgressMonitor progressMonitor);
}
