package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;

import org.itsallcode.whiterabbit.jfxui.OsCheck;
import org.itsallcode.whiterabbit.jfxui.OsCheck.OSType;

public interface Tray
{
    static Tray create(TrayCallback callback)
    {
        return SwingUtil.runOnSwingThread(() -> {
            if (!isAwtSystemTraySupported())
            {
                return new DummyTrayIcon();
            }

            return AwtTrayIcon.createAwtTray(callback);
        });
    }

    static boolean isAwtSystemTraySupported()
    {
        return SystemTray.isSupported() && new OsCheck().getOperatingSystemType() != OSType.MACOS;
    }

    void displayMessage(String caption, String text, MessageType messageType);

    void removeTrayIcon();

    boolean isSupported();

    void setTooltip(String tooltip);
}
