package org.itsallcode.whiterabbit.plugin.csv;

import org.itsallcode.whiterabbit.api.AbstractPlugin;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;

public class CSVExporterPlugin extends AbstractPlugin<ProjectReportExporter>
{
    public CSVExporterPlugin()
    {
        super("csv", ProjectReportExporter.class);
    }

    @Override
    protected CSVProjectReportExporter createInstance()
    {
        final CSVConfig csvConfig = new CSVConfig(config);
        final OutStreamProvider outStreamProvider = new DirectoryStreamProvider(csvConfig.getOutPath());
        return new CSVProjectReportExporter(csvConfig, outStreamProvider);
    }
}
