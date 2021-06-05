package org.itsallcode.whiterabbit.plugin.csv;

import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.PluginFeature;

import java.nio.file.Paths;

public class CSVExporterPlugin implements Plugin
{

    private PluginConfiguration config;

    @Override
    public void init(PluginConfiguration config)
    {
        this.config = config;
    }

    @Override
    public String getId()
    {
        return "csv";
    }

    @Override
    public void close()
    {
        // ignore
    }

    @Override
    public boolean supports(Class<? extends PluginFeature> featureType)
    {
        return featureType.isAssignableFrom(CSVProjectReportExporter.class);
    }

    @Override
    public <T extends PluginFeature> T getFeature(Class<T> featureType)
    {
        if (featureType.isAssignableFrom(CSVProjectReportExporter.class))
        {
            CSVConfig csvConfig = new CSVConfig(config);
            final OutStreamProvider outStreamProvider = new DirectoryStreamProvider(csvConfig.getOutPath());
            return featureType.cast(new CSVProjectReportExporter(csvConfig, outStreamProvider));
        }
        throw new IllegalArgumentException("Feature " + featureType.getName() + " not supported by plugin " + getId());
    }
}
