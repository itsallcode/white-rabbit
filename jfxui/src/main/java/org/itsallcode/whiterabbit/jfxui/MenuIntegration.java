package org.itsallcode.whiterabbit.jfxui;

import java.awt.Desktop;
import java.awt.desktop.AboutEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.tray.OsCheck;
import org.itsallcode.whiterabbit.jfxui.tray.OsCheck.OSType;

class MenuIntegration
{
    private static final Logger LOG = LogManager.getLogger(MenuIntegration.class);
    private static final MenuIntegration INSTANCE = new MenuIntegration();
    private UiActions actions;

    static MenuIntegration getInstance()
    {
        return INSTANCE;
    }

    void setUiActions(UiActions actions)
    {
        this.actions = actions;
    }

    void register()
    {
        if (!supported())
        {
            LOG.debug("Menu integration not supported");
            return;
        }

        LOG.debug("Registering menu integration");
        final Desktop desktop = Desktop.getDesktop();
        desktop.setAboutHandler(this::showAboutDialog);
    }

    private void showAboutDialog(AboutEvent e)
    {
        getActions().showAboutDialog();
    }

    private UiActions getActions()
    {
        if (actions == null)
        {
            throw new IllegalStateException("UI Actions not registered");
        }
        return actions;
    }

    private static boolean supported()
    {
        return Desktop.isDesktopSupported() && OsCheck.getOperatingSystemType() == OSType.MACOS;
    }
}
