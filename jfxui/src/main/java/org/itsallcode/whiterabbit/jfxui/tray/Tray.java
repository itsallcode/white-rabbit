package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Tray
{
    static final Logger LOG = LogManager.getLogger(Tray.class);

    public static Tray create(TrayCallback callback)
    {
        return SwingUtil.runOnFxApplicationThread(() -> {
            ensureToolkitInitialized();
            if (!SystemTray.isSupported())
            {
                LOG.warn("No system tray support.");
                return new DummyTrayIcon();
            }

            return AwtTrayIcon.createAwtTray(callback);
        });
    }

    private static void ensureToolkitInitialized()
    {
        Toolkit.getDefaultToolkit();
    }

    void displayMessage(String caption, String text, MessageType messageType);

    void removeTrayIcon();
}
