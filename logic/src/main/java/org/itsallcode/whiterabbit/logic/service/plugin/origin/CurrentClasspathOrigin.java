package org.itsallcode.whiterabbit.logic.service.plugin.origin;

class ClassPathPluginOrigin extends PluginOrigin
{
    public ClassPathPluginOrigin(ClassLoader classLoader)
    {
        super(classLoader);
    }

    public String getDescription()
    {
        return "included";
    }

    @Override
    public boolean isExternal()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "ClassPath origin";
    }
}