package org.itsallcode.whiterabbit.logic.service.plugin;

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

final class AppPluginImpl implements AppPlugin
{
    private static final Logger LOG = LogManager.getLogger(AppPluginImpl.class);

    private final AppPluginOrigin origin;
    private final Plugin plugin;
    private final Config config;

    private AppPluginImpl(final Config config, final AppPluginOrigin origin, final Plugin plugin)
    {
        this.config = config;
        this.origin = origin;
        this.plugin = plugin;
    }

    public static AppPluginImpl create(final Config config, final AppPluginOrigin origin, final Plugin plugin)
    {
        return new AppPluginImpl(config, origin, plugin);
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
                .toList();
    }

    @Override
    public AppPluginOrigin getOrigin()
    {
        return origin;
    }

    public boolean supports(final Class<? extends PluginFeature> featureType)
    {
        try
        {
            return plugin.supports(featureType);
        }
        catch (final RuntimeException e)
        {
            LOG.warn("Error loading plugin '{}'", getId(), e);
            return false;
        }
    }

    @Override
    public <T extends PluginFeature> Optional<T> getFeature(final Class<T> featureType)
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
        private String prefixed(final String key)
        {
            return plugin.getId() + "." + key;
        }

        @Override
        public String getMandatoryValue(final String key)
        {
            return config.getMandatoryValue(prefixed(key));
        }

        @Override
        public Optional<String> getOptionalValue(final String key)
        {
            return config.getOptionalValue(prefixed(key));
        }

        @Override
        public Path getDataDir()
        {
            return config.getDataDir();
        }
    }
}
