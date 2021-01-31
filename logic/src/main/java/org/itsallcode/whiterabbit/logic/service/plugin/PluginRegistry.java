package org.itsallcode.whiterabbit.logic.service.plugin;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.logic.Config;

class PluginRegistry
{
    private static final Logger LOG = LogManager.getLogger(PluginRegistry.class);

    private Map<String, PluginWrapper> plugins = new HashMap<>();
    private final Config config;

    PluginRegistry(Config config)
    {
        this.config = config;
    }

    void load()
    {
        closeAllPlugins();
        plugins = loadPlugins();
    }

    private Map<String, PluginWrapper> loadPlugins()
    {
        return Stream.concat(pluginsFromClasspath(), pluginsFromJars())
                .collect(toMap(PluginWrapper::getId, Function.identity()));
    }

    private Stream<PluginWrapper> pluginsFromJars()
    {
        return getPluginJars().stream().flatMap(this::loadPlugins);
    }

    private Stream<PluginWrapper> pluginsFromClasspath()
    {
        return loadPlugins(getClass().getClassLoader());
    }

    private Stream<PluginWrapper> loadPlugins(Path jar)
    {
        return loadPlugins(createClassLoader(jar));
    }

    private Stream<PluginWrapper> loadPlugins(final ClassLoader classLoader)
    {
        final ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(Plugin.class, classLoader);
        return serviceLoader.stream()
                .flatMap(provider -> loadPlugin(provider, classLoader).stream());
    }

    private ClassLoader createClassLoader(Path jar)
    {
        final String name = "PluginClassLoader-" + jar.getFileName();
        final URL[] urls = new URL[] { toUrl(jar) };
        return new URLClassLoader(name, urls, getClass().getClassLoader());
    }

    private List<Path> getPluginJars()
    {
        final Path pluginDir = config.getPluginDir();
        if (!Files.exists(pluginDir))
        {
            LOG.info("Plugin directory {} does not exist", pluginDir);
            return emptyList();
        }
        LOG.info("Searching plugin jars in {}", pluginDir);
        try (Stream<Path> stream = Files.list(pluginDir))
        {
            return stream.filter(file -> file.getFileName().toString().endsWith(".jar"))
                    .collect(toList());
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

    private Optional<PluginWrapper> loadPlugin(Provider<Plugin> provider, ClassLoader pluginClassLoader)
    {
        LOG.info("Loading plugin {} using {}", provider.type(), pluginClassLoader);
        try
        {
            final Plugin pluginInstance = provider.get();
            final PluginWrapper pluginWrapper = PluginWrapper.create(config, pluginClassLoader, pluginInstance);
            pluginWrapper.init();
            return Optional.of(pluginWrapper);
        }
        catch (final RuntimeException e)
        {
            LOG.warn("Error loading plugin {} using {}", provider.type(), pluginClassLoader, e);
            return Optional.empty();
        }
    }

    Collection<PluginWrapper> getAllPlugins()
    {
        return plugins.values();
    }

    PluginWrapper getPlugin(String id)
    {
        final PluginWrapper plugin = plugins.get(id);
        if (plugin == null)
        {
            throw new IllegalStateException("Plugin '" + id + "' not found. Available plugins: " + plugins.keySet());
        }
        return plugin;
    }

    private void closeAllPlugins()
    {
        if (plugins.isEmpty())
        {
            return;
        }
        LOG.debug("Closing {} plugins...", plugins.size());
        plugins.values().forEach(PluginWrapper::close);
    }

    public void close()
    {
        closeAllPlugins();
    }
}
