package org.itsallcode.whiterabbit.logic.service.plugin.origin;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import org.itsallcode.whiterabbit.logic.service.plugin.AppPlugin.PluginOrigin;

public abstract class AbstractPluginOrigin implements PluginOrigin
{

    private ClassLoader classLoader;

    public AbstractPluginOrigin(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public static AbstractPluginOrigin forCurrentClassPath()
    {
        return new ClassPathPluginOrigin(getBaseClassLoader());
    }

    public static AbstractPluginOrigin forJar(Path jar)
    {
        return new JarPluginOrigin(jar, createClassLoader(jar));
    }

    private static ClassLoader getBaseClassLoader()
    {
        return AbstractPluginOrigin.class.getClassLoader();
    }

    private static ClassLoader createClassLoader(Path jar)
    {
        final String name = "PluginClassLoader-" + jar.getFileName();
        final URL[] urls = new URL[] { toUrl(jar) };
        return new URLClassLoader(name, urls, getBaseClassLoader());
    }

    private static URL toUrl(Path path)
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

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

}
