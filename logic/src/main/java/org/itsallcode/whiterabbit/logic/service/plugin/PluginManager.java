package org.itsallcode.whiterabbit.logic.service.plugin;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage;
import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
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

    public List<String> getProjectReportExporterPlugins()
    {
        return findPluginsSupporting(ProjectReportExporter.class);
    }

    private List<String> findPluginsSupporting(Class<? extends PluginFeature> featureType)
    {
        return pluginRegistry.getAllPlugins().stream()
                .filter(plugin -> plugin.supports(featureType))
                .map(PluginWrapper::getId)
                .collect(toList());
    }

    public ProjectReportExporter getProjectReportExporter(String id)
    {
        return getFeature(id, ProjectReportExporter.class);
    }

    public Optional<MonthDataStorage> getMonthDataStorage()
    {
        return getUniqueFeature(MonthDataStorage.class);
    }

    private Optional<MonthDataStorage> getUniqueFeature(Class<MonthDataStorage> featureType)
    {
        final List<String> pluginIds = findPluginsSupporting(featureType);
        if (pluginIds.isEmpty())
        {
            return Optional.empty();
        }
        if (pluginIds.size() > 1)
        {
            throw new IllegalStateException("Found multiple plugins supporting " + featureType.getName()
                    + ": " + pluginIds + ". Please add only one storage plugin to the classpath.");
        }
        return Optional.of(getFeature(pluginIds.get(0), featureType));
    }

    private <T extends PluginFeature> T getFeature(String id, final Class<T> featureType)
    {
        final PluginWrapper plugin = pluginRegistry.getPlugin(id);
        if (plugin == null)
        {
            throw new IllegalStateException("Plugin '" + id + "' not found");
        }
        if (!plugin.supports(featureType))
        {
            throw new IllegalStateException("Plugin '" + id + "' does not support feature " + featureType.getName());
        }
        return plugin.getFeature(featureType);
    }

    public void close()
    {
        pluginRegistry.close();
    }
}
