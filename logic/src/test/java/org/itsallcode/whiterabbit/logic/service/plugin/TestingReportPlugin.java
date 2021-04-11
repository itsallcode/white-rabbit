package org.itsallcode.whiterabbit.logic.service.plugin;

import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.model.ProjectReport;

public class TestingReportPlugin implements Plugin, ProjectReportExporter
{
    static final String PLUGIN_ID = "testingReportPlugin";
    private PluginConfiguration config;
    private boolean closed = false;

    @Override
    public void init(PluginConfiguration config)
    {
        this.config = config;
    }

    @Override
    public String getId()
    {
        return PLUGIN_ID;
    }

    @Override
    public void close()
    {
        this.closed = true;
    }

    @Override
    public boolean supports(Class<? extends PluginFeature> featureType)
    {
        return featureType.isInstance(this);
    }

    @Override
    public <T extends PluginFeature> T getFeature(Class<T> featureType)
    {
        return featureType.cast(this);
    }

    public PluginConfiguration getConfig()
    {
        return config;
    }

    public boolean isClosed()
    {
        return closed;
    }

    @Override
    public void export(ProjectReport report, ProgressMonitor progressMonitor)
    {

    }
}
