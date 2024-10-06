package org.itsallcode.whiterabbit.logic.service.plugin.origin;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import org.itsallcode.whiterabbit.logic.service.plugin.AppPlugin.AppPluginOrigin;

public abstract class PluginOrigin implements AppPluginOrigin
{
    private final ClassLoader classLoader;

    protected PluginOrigin(final ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public static PluginOrigin forCurrentClassPath()
    {
        return new ClasspathPluginOrigin(getBaseClassLoader());
    }

    public static PluginOrigin forJar(final Path jar)
    {
        return new JarPluginOrigin(jar, createClassLoader(jar));
    }

    private static ClassLoader getBaseClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }

    private static ClassLoader createClassLoader(final Path jar)
    {
        final String name = "PluginClassLoader-" + jar.getFileName();
        final URL[] urls = new URL[] { toUrl(jar) };
        return new URLClassLoader(name, urls, getBaseClassLoader());
    }

    private static URL toUrl(final Path path)
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
