package org.itsallcode.whiterabbit.plugin.demo;

import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.PluginFeature;

public class DemoPlugin implements Plugin
{
    @Override
    public void init(PluginConfiguration config)
    {
        // ignore
    }

    @Override
    public String getId()
    {
        return "demo";
    }

    @Override
    public void close()
    {
        // ignore
    }

    @Override
    public boolean supports(Class<? extends PluginFeature> featureType)
    {
        return featureType.isAssignableFrom(DemoProjectReportExporter.class);
    }

    @Override
    public <T extends PluginFeature> T getFeature(Class<T> featureType)
    {
        if (featureType.isAssignableFrom(DemoProjectReportExporter.class))
        {
            return featureType.cast(new DemoProjectReportExporter());
        }
        throw new IllegalArgumentException("Feature " + featureType.getName() + " not supported by plugin " + getId());
    }
}
