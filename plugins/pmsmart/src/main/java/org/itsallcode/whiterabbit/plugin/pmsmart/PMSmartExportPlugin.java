package org.itsallcode.whiterabbit.plugin.pmsmart;

import java.util.Optional;

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
    public <T extends PluginFeature> Optional<T> getFeature(Class<T> featureType)
    {
        if (featureType.isAssignableFrom(PMSmartExporter.class))
        {
            return Optional.of(featureType.cast(new PMSmartExporter(config, new WebDriverFactory())));
        }
        return Optional.empty();
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
