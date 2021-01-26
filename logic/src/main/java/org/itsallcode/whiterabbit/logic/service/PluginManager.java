package org.itsallcode.whiterabbit.logic.service;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.plugin.PluginConfiguration;
import org.itsallcode.whiterabbit.plugin.ProjectReportExporter;
import org.itsallcode.whiterabbit.plugin.WhiteRabbitPlugin;

public class PluginManager
{
    private static final Logger LOG = LogManager.getLogger(PluginManager.class);
    private final Map<String, WhiteRabbitPlugin> plugins;

    public PluginManager(Map<String, WhiteRabbitPlugin> plugins)
    {
        this.plugins = plugins;
    }

    public static PluginManager create(Config config)
    {
        final PluginConfiguration pluginConfig = new PluginConfigImpl(config);
        final ServiceLoader<WhiteRabbitPlugin> serviceLoader = ServiceLoader.load(WhiteRabbitPlugin.class);
        final Map<String, WhiteRabbitPlugin> plugins = serviceLoader.stream()
                .map(provider -> loadPlugin(pluginConfig, provider))
                .collect(toMap(WhiteRabbitPlugin::getId, Function.identity()));
        return new PluginManager(plugins);
    }

    private static WhiteRabbitPlugin loadPlugin(PluginConfiguration pluginConfig, Provider<WhiteRabbitPlugin> provider)
    {
        LOG.info("Loading plugin {}", provider.type());
        final WhiteRabbitPlugin plugin = provider.get();
        plugin.init(pluginConfig);
        return plugin;
    }

    public ProjectReportExporter getProjectReportExporter(String id)
    {
        return getPlugin(id).projectReportExporter()
                .orElseThrow(
                        () -> new IllegalStateException("Plugin " + id + " does not support project report exporter"));
    }

    private WhiteRabbitPlugin getPlugin(String id)
    {
        final WhiteRabbitPlugin plugin = plugins.get(id);
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
