package org.itsallcode.whiterabbit.logic.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.Plugin;
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
        return pluginRegistry.getAllPlugins().stream()
                .filter(plugin -> plugin.projectReportExporter().isPresent())
                .map(Plugin::getId)
                .collect(toList());
    }

    public ProjectReportExporter getProjectReportExporter(String id)
    {
        return pluginRegistry.getPlugin(id).projectReportExporter()
                .orElseThrow(
                        () -> new IllegalStateException("Plugin " + id + " does not support project report exporter"));
    }

    public void close()
    {
        pluginRegistry.close();
    }
}
