package org.itsallcode.whiterabbit.logic.service.plugin;

import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.PluginFeature;

public class TestingPlugin implements Plugin, PluginFeature
{
    static final String PLUGIN_ID = "testingPlugin";
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
        return TestingPlugin.class.isAssignableFrom(featureType);
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
}
