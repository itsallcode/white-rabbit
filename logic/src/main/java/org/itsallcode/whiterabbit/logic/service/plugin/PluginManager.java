package org.itsallcode.whiterabbit.logic.service.plugin;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.itsallcode.whiterabbit.logic.Config;

public class PluginManager
{
    private static final Logger LOG = LogManager.getLogger(PluginManager.class);
    private final PluginRegistry pluginRegistry;

    PluginManager(PluginRegistry pluginRegistry)
    {
        this.pluginRegistry = pluginRegistry;
    }

    public static PluginManager create(Config config)
    {
        final PluginRegistry pluginRegistry = new PluginRegistry(config);
        pluginRegistry.load();
        LOG.info("Loaded {} plugins", pluginRegistry.getAllPlugins().size());
        return new PluginManager(pluginRegistry);
    }

    public <T extends PluginFeature> List<T> getAllFeatures(Class<T> featureType)
    {
        return findPluginsSupporting(featureType).stream()
                .map(plugin -> plugin.getFeature(featureType))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    public List<PluginWrapper> findPluginsSupporting(Class<? extends PluginFeature> featureType)
    {
        return pluginRegistry.getAllPlugins().stream()
                .filter(plugin -> plugin.supports(featureType))
                .collect(toList());
    }

    @SuppressWarnings("java:S1452") // Use generic wildcard as return type.
    public Collection<? extends AppPlugin> getAllPlugins()
    {
        return pluginRegistry.getAllPlugins();
    }

    public <T extends PluginFeature> Optional<T> getUniqueFeature(Class<T> featureType)
    {
        final List<PluginWrapper> plugins = findPluginsSupporting(featureType);
        if (plugins.isEmpty())
        {
            return Optional.empty();
        }
        if (plugins.size() > 1)
        {
            throw new IllegalStateException("Found multiple plugins supporting " + featureType.getName()
                    + ": " + plugins + ". Please add only one storage plugin to the classpath.");
        }
        return plugins.get(0).getFeature(featureType);
    }

    public void close()
    {
        pluginRegistry.close();
    }
}
