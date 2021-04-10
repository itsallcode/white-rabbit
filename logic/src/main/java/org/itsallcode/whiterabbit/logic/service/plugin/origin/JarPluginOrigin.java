package org.itsallcode.whiterabbit.logic.service.plugin.origin;

import java.nio.file.Path;

class JarPluginOrigin extends PluginOrigin
{
    private final Path jar;

    public JarPluginOrigin(Path jar, ClassLoader classLoader)
    {
        super(classLoader);
        this.jar = jar;
    }

    public String getDescription()
    {
        return jar.toString();
    }

    @Override
    public boolean isExternal()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "Jar origin [" + jar + "]";
    }
}