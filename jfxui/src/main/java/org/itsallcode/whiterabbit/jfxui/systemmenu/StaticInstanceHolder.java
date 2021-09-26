package org.itsallcode.whiterabbit.jfxui.systemmenu;

import java.awt.Desktop;

import org.itsallcode.whiterabbit.jfxui.tray.OsCheck;

class StaticInstanceHolder
{
    private static MenuIntegration instance;

    private StaticInstanceHolder()
    {
        // not instantiable
    }

    static MenuIntegration getInstance()
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

        MenuIntegration createInstance()
        {
            if (osCheck.supportsSystemMenuBar() && osCheck.isDesktopSupported())
            {
                return new MacMenuIntegration(Desktop.getDesktop());
            }
            else
            {
                return new UnsupportedMenuIntegration();
            }
        }
    }
}
