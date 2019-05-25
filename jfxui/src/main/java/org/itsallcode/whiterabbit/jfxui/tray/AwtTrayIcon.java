package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import javax.imageio.ImageIO;
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

    public static Tray createAwtTray(TrayCallback callback)
    {
        try
        {
            final SystemTray tray = SystemTray.getSystemTray();
            final Image scaledIcon = loadImage("/icon.png", tray.getTrayIconSize());
            final TrayIcon trayIcon = new TrayIcon(scaledIcon, "White Rabbit Time Recording",
                    createPopupMenu(callback));
            tray.add(trayIcon);
            trayIcon.addActionListener(event -> callback.showMainWindow());
            return new AwtTrayIcon(tray, trayIcon);
        }
        catch (final Exception e)
        {
            throw new IllegalStateException("Error creating system tray: " + e.getMessage(), e);
        }
    }

    private static Image loadImage(final String resourceName, Dimension size)
    {
        final URL imageUrl = AwtTrayIcon.class.getResource(resourceName);
        try
        {
            final Image image = ImageIO.read(imageUrl);
            LOG.debug("Scaling icon {} to {}", imageUrl, size);
            return image.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error loading image " + resourceName, e);
        }
    }

    private static PopupMenu createPopupMenu(TrayCallback callback)
    {
        final PopupMenu popupMenu = new PopupMenu("White Rabbit Time Recording");
        popupMenu.add(menuItem("Show", KeyEvent.VK_S, callback::showMainWindow));
        popupMenu.add(menuItem("Interruption", KeyEvent.VK_I, callback::startInterruption));
        popupMenu.addSeparator();
        popupMenu.add(menuItem("Exit", KeyEvent.VK_E, callback::exit));
        return popupMenu;
    }

    private static MenuItem menuItem(String label, int shortcutKey, Runnable action)
    {
        final MenuItem menuItem = new MenuItem(label, new MenuShortcut(shortcutKey));
        menuItem.addActionListener(event -> action.run());
        return menuItem;
    }

    @Override
    public void displayMessage(String caption, String text, MessageType messageType)
    {
        SwingUtilities.invokeLater(() -> trayIcon.displayMessage(caption, text, messageType));
    }

    @Override
    public void removeTrayIcon()
    {
        tray.remove(trayIcon);
    }
}
