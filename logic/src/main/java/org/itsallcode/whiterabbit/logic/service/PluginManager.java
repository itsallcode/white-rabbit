package org.itsallcode.whiterabbit.logic.service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.logic.Config;

public class PluginManager
{
    private static final Logger LOG = LogManager.getLogger(PluginManager.class);
    private final Map<String, Plugin> plugins;

    public PluginManager(Map<String, Plugin> plugins)
    {
        this.plugins = plugins;
    }

    public static PluginManager create(Config config)
    {
        final PluginConfiguration pluginConfig = new PluginConfigImpl(config);
        final ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(Plugin.class);
        final Map<String, Plugin> plugins = serviceLoader.stream()
                .map(provider -> loadPlugin(pluginConfig, provider))
                .collect(toMap(Plugin::getId, Function.identity()));
        return new PluginManager(plugins);
    }

    private static Plugin loadPlugin(PluginConfiguration pluginConfig, Provider<Plugin> provider)
    {
        LOG.info("Loading plugin {}", provider.type());
        final Plugin plugin = provider.get();
        plugin.init(pluginConfig);
        return plugin;
    }

    public List<String> getProjectReportExporterPlugins()
    {
        return this.plugins.values().stream()
                .filter(plugin -> plugin.projectReportExporter().isPresent())
                .map(Plugin::getId)
                .collect(toList());
    }

    public ProjectReportExporter getProjectReportExporter(String id)
    {
        return getPlugin(id).projectReportExporter()
                .orElseThrow(
                        () -> new IllegalStateException("Plugin " + id + " does not support project report exporter"));
    }

    private Plugin getPlugin(String id)
    {
        final Plugin plugin = plugins.get(id);
        if (plugin == null)
        {
            throw new IllegalStateException("Plugin '" + id + "' not found. Available plugins: " + plugins.keySet());
        }
        return plugin;
    }

    private static class PluginConfigImpl implements PluginConfiguration
    {

        private final Config config;

        public PluginConfigImpl(Config config)
        {
            this.config = config;
        }

        @Override
        public String getMandatoryValue(String key)
        {
            return config.getMandatoryValue(key);
        }
    }
}
