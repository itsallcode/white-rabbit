package org.itsallcode.whiterabbit.logic.service.plugin;

import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.itsallcode.whiterabbit.logic.Config;

class PluginWrapper implements AppPlugin
{
    private static final Logger LOG = LogManager.getLogger(PluginWrapper.class);

    private final AppPluginOrigin origin;
    private final Plugin plugin;
    private final Config config;

    private PluginWrapper(Config config, AppPluginOrigin origin, Plugin plugin)
    {
        this.config = config;
        this.origin = origin;
        this.plugin = plugin;
    }

    public static PluginWrapper create(Config config, AppPluginOrigin origin, Plugin plugin)
    {
        return new PluginWrapper(config, origin, plugin);
    }

    void init()
    {
        plugin.init(new PluginConfigImpl());
    }

    @Override
    public String getId()
    {
        return plugin.getId();
    }

    @Override
    public Collection<AppPluginFeature> getFeatures()
    {
        return Stream.of(AppPluginFeature.values())
                .filter(feature -> supports(feature.getFeatureClass()))
                .collect(toList());
    }

    @Override
    public AppPluginOrigin getOrigin()
    {
        return origin;
    }

    public boolean supports(Class<? extends PluginFeature> featureType)
    {
        try
        {
            return plugin.supports(featureType);
        }
        catch (final Exception e)
        {
            LOG.warn("Error loading plugin '{}'", getId(), e);
            return false;
        }
    }

    <T extends PluginFeature> T getFeature(Class<T> featureType)
    {
        return plugin.getFeature(featureType);
    }

    boolean isLoadedFromExternalJar()
    {
        return origin.isExternal();
    }

    void close()
    {
        plugin.close();
    }

    @Override
    public String toString()
    {
        return "PluginWrapper [origin=" + origin + ", plugin=" + plugin + "]";
    }

    private class PluginConfigImpl implements PluginConfiguration
    {
        private String prefixed(String key)
        {
            return plugin.getId() + "." + key;
        }

        @Override
        public String getMandatoryValue(String key)
        {
            return config.getMandatoryValue(prefixed(key));
        }

        @Override
        public Optional<String> getOptionalValue(String key)
        {
            return config.getOptionalValue(prefixed(key));
        }

        @Override
        public Path getUserDir()
        {
            return config.getUserDir();
        }
    }
}
