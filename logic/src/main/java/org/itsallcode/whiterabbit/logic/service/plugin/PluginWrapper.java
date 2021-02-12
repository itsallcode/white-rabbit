package org.itsallcode.whiterabbit.logic.service.plugin;

import java.net.URLClassLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.itsallcode.whiterabbit.logic.Config;

class PluginWrapper
{
    private static final Logger LOG = LogManager.getLogger(PluginWrapper.class);

    private final ClassLoader classLoader;
    private final Plugin plugin;
    private final Config config;

    private PluginWrapper(Config config, ClassLoader pluginClassLoader, Plugin plugin)
    {
        this.config = config;
        this.classLoader = pluginClassLoader;
        this.plugin = plugin;
    }

    public static PluginWrapper create(Config config, ClassLoader pluginClassLoader, Plugin plugin)
    {
        return new PluginWrapper(config, pluginClassLoader, plugin);
    }

    void init()
    {
        plugin.init(new PluginConfigImpl());
    }

    String getId()
    {
        return plugin.getId();
    }

    boolean supports(Class<? extends PluginFeature> featureType)
    {
        try
        {
            return plugin.supports(featureType);
        }
        catch (final Exception e)
        {
            LOG.warn("Error loading plugin '{}'", getId(), e);
            return false;
        }
    }

    <T extends PluginFeature> T getFeature(Class<T> featureType)
    {
        return plugin.getFeature(featureType);
    }

    boolean isLoadedFromExternalJar()
    {
        return URLClassLoader.class.isInstance(classLoader);
    }

    void close()
    {
        plugin.close();
    }

    @Override
    public String toString()
    {
        return "PluginWrapper [classLoader=" + classLoader + ", plugin=" + plugin + "]";
    }

    private class PluginConfigImpl implements PluginConfiguration
    {
        @Override
        public String getMandatoryValue(String key)
        {
            return config.getMandatoryValue(plugin.getId() + "." + key);
        }
    }
}
