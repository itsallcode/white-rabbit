package org.itsallcode.whiterabbit.logic.service;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.logic.Config;

class PluginRegistry
{
    private static final Logger LOG = LogManager.getLogger(PluginRegistry.class);

    private Map<String, Plugin> plugins = new HashMap<>();
    private final Config config;
    private final PluginConfigImpl pluginConfig;

    PluginRegistry(Config config)
    {
        this.config = config;
        this.pluginConfig = new PluginConfigImpl(config);
    }

    void load()
    {
        plugins = loadPlugins();
    }

    private Map<String, Plugin> loadPlugins()
    {
        closeAllPlugins();
        final ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(Plugin.class, createClassLoader());
        return serviceLoader.stream()
                .map(this::loadPlugin)
                .collect(toMap(Plugin::getId, Function.identity()));
    }

    private ClassLoader createClassLoader()
    {
        final URL[] urls = getPluginClasspath();
        if (LOG.isInfoEnabled())
        {
            LOG.info("Found {} plugin jars: {}", urls.length, Arrays.toString(urls));
        }
        return new URLClassLoader("PluginClassLoader", urls, getClass().getClassLoader());
    }

    private URL[] getPluginClasspath()
    {
        final Path pluginDir = config.getPluginDir();
        if (!Files.exists(pluginDir))
        {
            LOG.info("Plugin directory {} does not exist", pluginDir);
            return new URL[0];
        }
        LOG.info("Searching plugin jars in {}", pluginDir);
        try (Stream<Path> stream = Files.list(pluginDir))
        {
            return stream.filter(file -> file.getFileName().toString().endsWith(".jar"))
                    .map(this::toUrl)
                    .toArray(URL[]::new);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error listing plugins in " + pluginDir, e);
        }
    }

    private URL toUrl(Path path)
    {
        try
        {
            return path.toUri().toURL();
        }
        catch (final MalformedURLException e)
        {
            throw new IllegalStateException("Error converting path " + path + " to url", e);
        }
    }

    private Plugin loadPlugin(Provider<Plugin> provider)
    {
        LOG.info("Loading plugin {}", provider.type());
        final Plugin plugin = provider.get();
        plugin.init(pluginConfig);
        return plugin;
    }

    Collection<Plugin> getAllPlugins()
    {
        return plugins.values();
    }

    Plugin getPlugin(String id)
    {
        final Plugin plugin = plugins.get(id);
        if (plugin == null)
        {
            throw new IllegalStateException("Plugin '" + id + "' not found. Available plugins: " + plugins.keySet());
        }
        return plugin;
    }

    private void closeAllPlugins()
    {
        LOG.debug("Closing {} plugins...", plugins.size());
        plugins.values().forEach(Plugin::close);
    }

    public void close()
    {
        closeAllPlugins();
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
