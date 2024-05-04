package org.itsallcode.whiterabbit.logic.service.plugin;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.service.plugin.origin.PluginOrigin;

class PluginRegistry
{
    private static final Logger LOG = LogManager.getLogger(PluginRegistry.class);

    private Map<String, AppPluginImpl> plugins = new HashMap<>();
    private final Config config;

    PluginRegistry(final Config config)
    {
        this.config = config;
    }

    void load()
    {
        closeAllPlugins();
        plugins = loadPlugins();
    }

    private Map<String, AppPluginImpl> loadPlugins()
    {
        return Stream.concat(pluginsFromClasspath(), pluginsFromJars())
                .collect(toMap(AppPluginImpl::getId, Function.identity(), preferExternalJars()));
    }

    private static BinaryOperator<AppPluginImpl> preferExternalJars()
    {
        return (a, b) -> {
            LOG.warn("Found two plugins with same ID '{}':\n- {}\n- {}", a.getId(), a, b);
            if (b.isLoadedFromExternalJar())
            {
                return b;
            }
            return a;
        };
    }

    private Stream<AppPluginImpl> pluginsFromJars()
    {
        return getPluginJars().stream().flatMap(this::loadPlugins);
    }

    private Stream<AppPluginImpl> pluginsFromClasspath()
    {
        return loadPlugins(PluginOrigin.forCurrentClassPath());
    }

    private Stream<AppPluginImpl> loadPlugins(final Path jar)
    {
        return loadPlugins(PluginOrigin.forJar(jar));
    }

    private Stream<AppPluginImpl> loadPlugins(final PluginOrigin origin)
    {
        final ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(Plugin.class, origin.getClassLoader());
        return serviceLoader.stream()
                .flatMap(provider -> loadPlugin(provider, origin).stream());
    }

    private List<Path> getPluginJars()
    {
        final Path pluginDir = config.getPluginDir();
        if (pluginDir == null || !Files.exists(pluginDir))
        {
            LOG.debug("Plugin directory {} does not exist", pluginDir);
            return emptyList();
        }
        LOG.debug("Searching plugin jars in {}", pluginDir);
        try (Stream<Path> stream = Files.list(pluginDir))
        {
            return stream.filter(file -> file.getFileName().toString().endsWith(".jar"))
                    .toList();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error listing plugins in '" + pluginDir + "'", e);
        }
    }

    private Optional<AppPluginImpl> loadPlugin(final Provider<Plugin> provider, final PluginOrigin origin)
    {
        LOG.debug("Loading plugin {} using {}", provider.type().getName(), origin);
        try
        {
            final Plugin pluginInstance = provider.get();
            final AppPluginImpl pluginWrapper = AppPluginImpl.create(config, origin, pluginInstance);
            pluginWrapper.init();
            return Optional.of(pluginWrapper);
        }
        catch (final RuntimeException e)
        {
            LOG.warn("Error loading plugin {} using {}", provider.type().getName(), origin, e);
            return Optional.empty();
        }
    }

    Collection<AppPluginImpl> getAllPlugins()
    {
        return plugins.values();
    }

    AppPluginImpl getPlugin(final String id)
    {
        final AppPluginImpl plugin = plugins.get(id);
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
        plugins.values().forEach(AppPluginImpl::close);
    }

    public void close()
    {
        closeAllPlugins();
    }
}
