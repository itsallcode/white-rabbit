package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;

public interface Tray
{
    public static Tray create(TrayCallback callback)
    {
        return SwingUtil.runOnSwingThread(() -> {
            if (!SystemTray.isSupported())
            {
                return new DummyTrayIcon();
            }

            return AwtTrayIcon.createAwtTray(callback);
        });
    }

    void displayMessage(String caption, String text, MessageType messageType);

    void removeTrayIcon();

    boolean isSupported();

    void setTooltip(String tooltip);
}
