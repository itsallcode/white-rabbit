package org.itsallcode.whiterabbit.logic.service.plugin;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.ProjectReportExporter;
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

    private List<String> findPluginsSupporting(Class<?> featureType)
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

    private <T> T getFeature(String id, final Class<T> featureType)
    {
        return pluginRegistry.getPlugin(id).getFeature(featureType);
    }

    public void close()
    {
        pluginRegistry.close();
    }
}
