package org.itsallcode.whiterabbit.jfxui.systemmenu;

import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.UiActions;

class MacMenuIntegration implements MenuIntegration
{
    private static final Logger LOG = LogManager.getLogger(MacMenuIntegration.class);

    private final Desktop desktop;

    private UiActions actions;

    MacMenuIntegration(Desktop desktop)
    {
        this.desktop = desktop;
    }

    @Override
    public void register()
    {
        LOG.debug("Registering menu integration");
        desktop.setAboutHandler(this::showAboutDialog);
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
