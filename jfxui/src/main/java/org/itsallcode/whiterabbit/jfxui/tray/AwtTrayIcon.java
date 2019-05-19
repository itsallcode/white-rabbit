package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

class AwtTrayIcon implements Tray
{
    private final SystemTray tray;
    private final TrayIcon trayIcon;

    private AwtTrayIcon(SystemTray tray, TrayIcon trayIcon)
    {
        this.tray = tray;
        this.trayIcon = trayIcon;
    }

    public static Tray createAwtTray()
    {
        try
        {
            final SystemTray tray = SystemTray.getSystemTray();
            // final URL imageLoc = new URL("");
            // final Image image = ImageIO.read(imageLoc);
            final TrayIcon trayIcon = new TrayIcon(
                    new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB));
            tray.add(trayIcon);
            return new AwtTrayIcon(tray, trayIcon);
        }
        catch (final Exception e)
        {
            throw new IllegalStateException("Error creating system tray: " + e.getMessage(), e);
        }
    }

    @Override
    public void displayMessage(String caption, String text, MessageType messageType)
    {
        SwingUtilities.invokeLater(() -> trayIcon.displayMessage(caption, text, messageType));
    }
}
