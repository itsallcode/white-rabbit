package org.itsallcode.whiterabbit.logic.service.plugin.origin;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import org.itsallcode.whiterabbit.logic.service.plugin.AppPlugin.AppPluginOrigin;

public abstract class PluginOrigin implements AppPluginOrigin
{
    private ClassLoader classLoader;

    public PluginOrigin(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public static PluginOrigin forCurrentClassPath()
    {
        return new ClassPathPluginOrigin(getBaseClassLoader());
    }

    public static PluginOrigin forJar(Path jar)
    {
        return new JarPluginOrigin(jar, createClassLoader(jar));
    }

    private static ClassLoader getBaseClassLoader()
    {
        return PluginOrigin.class.getClassLoader();
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
