package org.itsallcode.whiterabbit.api;

import org.itsallcode.whiterabbit.api.model.IProjectReport;

public interface ProjectReportExporter
{
    void export(IProjectReport report, ProgressMonitor progressMonitor);
}
