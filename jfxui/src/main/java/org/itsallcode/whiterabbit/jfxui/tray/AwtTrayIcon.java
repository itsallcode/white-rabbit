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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class AwtTrayIcon implements Tray
{
    private static final Logger LOG = LogManager.getLogger(AwtTrayIcon.class);

    private final SystemTray tray;
    private final TrayIcon trayIcon;

    private AwtTrayIcon(final SystemTray tray, final TrayIcon trayIcon)
    {
        this.tray = tray;
        this.trayIcon = trayIcon;
    }

    static Tray createAwtTray(final TrayCallback callback)
    {
        try
        {
            final SystemTray tray = SystemTray.getSystemTray();
            final Image scaledIcon = loadImage("/icon.png", tray.getTrayIconSize());
            final TrayIcon trayIcon = new TrayIcon(scaledIcon, "White Rabbit Time Recording",
                    createPopupMenu(callback));
            LOG.info("Adding tray icon {}", trayIcon);
            tray.add(trayIcon);
            trayIcon.addActionListener(event -> callback.showMainWindow());
            return new AwtTrayIcon(tray, trayIcon);
        }
        catch (final Exception e)
        {
            throw new IllegalStateException("Error creating system tray: " + e.getMessage(), e);
        }
    }

    private static Image loadImage(final String resourceName, final Dimension size)
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

    private static PopupMenu createPopupMenu(final TrayCallback callback)
    {
        final PopupMenu popupMenu = new PopupMenu("White Rabbit Time Recording");
        popupMenu.add(menuItem("Show", KeyEvent.VK_S, callback::showMainWindow));
        popupMenu.add(menuItem("Interruption", KeyEvent.VK_I, callback::startInterruption));
        popupMenu.addSeparator();
        popupMenu.add(menuItem("Exit", KeyEvent.VK_E, callback::exit));
        return popupMenu;
    }

    private static MenuItem menuItem(final String label, final int shortcutKey, final Runnable action)
    {
        final MenuItem menuItem = new MenuItem(label, new MenuShortcut(shortcutKey));
        menuItem.addActionListener(event -> action.run());
        return menuItem;
    }

    @Override
    public void setTooltip(final String tooltip)
    {
        trayIcon.setToolTip(tooltip);
    }

    @Override
    public void displayMessage(final String caption, final String text, final MessageType messageType)
    {
        SwingUtilities.invokeLater(() -> trayIcon.displayMessage(caption, text, messageType));
    }

    @Override
    public void removeTrayIcon()
    {
        LOG.debug("Removing tray icon");
        tray.remove(trayIcon);
    }

    @Override
    public boolean isSupported()
    {
        return true;
    }
}
