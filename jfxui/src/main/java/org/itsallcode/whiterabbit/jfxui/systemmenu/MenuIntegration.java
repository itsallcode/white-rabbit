package org.itsallcode.whiterabbit.jfxui.systemmenu;

import org.itsallcode.whiterabbit.jfxui.UiActions;

public interface MenuIntegration
{
    public static MenuIntegration getInstance()
    {
        return StaticInstanceHolder.getInstance();
    }

    void register();

    void setUiActions(UiActions actions);
}