package org.itsallcode.whiterabbit.plugin.csv;

import org.itsallcode.whiterabbit.api.AbstractPlugin;

public class CSVExporterPlugin extends AbstractPlugin<CSVProjectReportExporter>
{
    public CSVExporterPlugin()
    {
        super("csv", CSVProjectReportExporter.class);
    }

    @Override
    protected CSVProjectReportExporter createInstance()
    {
        final CSVConfig csvConfig = new CSVConfig(config);
        final OutStreamProvider outStreamProvider = new DirectoryStreamProvider(csvConfig.getOutPath());
        return new CSVProjectReportExporter(csvConfig, outStreamProvider);
    }
}
