package org.itsallcode.whiterabbit.jfxui.systemmenu;

import org.itsallcode.whiterabbit.jfxui.UiActions;

public interface DesktopIntegration
{
    static DesktopIntegration getInstance()
    {
        return StaticInstanceHolder.getInstance();
    }

    void register();

    void setUiActions(UiActions actions);
}
