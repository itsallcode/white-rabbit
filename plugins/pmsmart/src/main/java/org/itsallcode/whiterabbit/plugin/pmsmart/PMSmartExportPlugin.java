package org.itsallcode.whiterabbit.plugin.pmsmart;

import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverFactory;

public class PMSmartExportPlugin implements Plugin
{
    private PluginConfiguration config;

    @Override
    public void init(PluginConfiguration config)
    {
        this.config = config;
    }

    @Override
    public boolean supports(Class<? extends PluginFeature> featureType)
    {
        return featureType.isAssignableFrom(PMSmartExporter.class);
    }

    @Override
    public <T extends PluginFeature> T getFeature(Class<T> featureType)
    {
        if (featureType.isAssignableFrom(PMSmartExporter.class))
        {
            return featureType.cast(new PMSmartExporter(config, new WebDriverFactory()));
        }
        throw new IllegalArgumentException("Feature " + featureType.getName() + " not supported by plugin " + getId());
    }

    @Override
    public String getId()
    {
        return "pmsmart";
    }

    @Override
    public void close()
    {
        // ignore
    }
}
