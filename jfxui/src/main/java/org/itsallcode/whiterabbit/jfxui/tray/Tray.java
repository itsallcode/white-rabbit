package org.itsallcode.whiterabbit.jfxui.tray;

public interface Tray
{
    void removeTrayIcon();

    boolean isSupported();

    void setTooltip(String tooltip);
}
