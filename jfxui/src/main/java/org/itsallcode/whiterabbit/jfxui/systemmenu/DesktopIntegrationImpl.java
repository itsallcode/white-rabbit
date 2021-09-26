package org.itsallcode.whiterabbit.jfxui.systemmenu;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.desktop.AboutEvent;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.UiActions;

class DesktopIntegrationImpl implements DesktopIntegration
{
    private static final Logger LOG = LogManager.getLogger(DesktopIntegrationImpl.class);

    private final Desktop desktop;

    private UiActions actions;

    DesktopIntegrationImpl(Desktop desktop)
    {
        this.desktop = desktop;
    }

    @Override
    public void register()
    {
        LOG.debug("Registering desktop integration");
        if (desktop.isSupported(Action.APP_ABOUT))
        {
            desktop.setAboutHandler(this::showAboutDialog);
        }
    }

    @Override
    public void setUiActions(UiActions actions)
    {
        this.actions = Objects.requireNonNull(actions);
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
}
