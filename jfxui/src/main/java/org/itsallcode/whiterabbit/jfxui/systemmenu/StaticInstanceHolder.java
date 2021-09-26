package org.itsallcode.whiterabbit.jfxui.systemmenu;

import java.awt.Desktop;

import org.itsallcode.whiterabbit.jfxui.OsCheck;

class StaticInstanceHolder
{
    private static DesktopIntegration instance;

    private StaticInstanceHolder()
    {
        // not instantiable
    }

    static DesktopIntegration getInstance()
    {
        if (instance == null)
        {
            instance = new InstanceFactory(new OsCheck()).createInstance();
        }
        return instance;
    }

    static class InstanceFactory
    {
        private final OsCheck osCheck;

        InstanceFactory(OsCheck osCheck)
        {
            this.osCheck = osCheck;
        }

        DesktopIntegration createInstance()
        {
            if (osCheck.isDesktopSupported())
            {
                return new DesktopIntegrationImpl(Desktop.getDesktop());
            }
            else
            {
                return new HeadlessDesktopIntegration();
            }
        }
    }
}
