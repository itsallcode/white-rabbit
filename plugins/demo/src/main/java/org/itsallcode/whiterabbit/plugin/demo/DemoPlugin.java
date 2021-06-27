package org.itsallcode.whiterabbit.plugin.demo;

import java.util.Optional;

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
    public <T extends PluginFeature> Optional<T> getFeature(Class<T> featureType)
    {
        if (featureType.isAssignableFrom(DemoProjectReportExporter.class))
        {
            return Optional.of(featureType.cast(new DemoProjectReportExporter()));
        }
        return Optional.empty();
    }
}
