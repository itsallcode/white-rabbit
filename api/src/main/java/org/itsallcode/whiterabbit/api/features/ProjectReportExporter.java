package org.itsallcode.whiterabbit.api.features;

import org.itsallcode.whiterabbit.api.model.ProjectReport;

/**
 * A {@link PluginFeature} that allows exporting a monthly {@link ProjectReport}
 * to another system.
 */
public interface ProjectReportExporter extends PluginFeature
{
    /**
     * Start the export.
     * 
     * @param report
     *            the report to export.
     * @param progressMonitor
     *            a progress monitor for reporting the export progress.
     */
    void export(ProjectReport report, ProgressMonitor progressMonitor);
}
