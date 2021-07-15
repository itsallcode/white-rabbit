package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.KeyEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;

class AwtTrayIcon implements Tray
{
    private static final Logger LOG = LogManager.getLogger(AwtTrayIcon.class);

    private final SystemTray tray;
    private final TrayIcon trayIcon;

    private AwtTrayIcon(SystemTray tray, TrayIcon trayIcon)
    {
        this.tray = tray;
        this.trayIcon = trayIcon;
    }

    static @NonNull Tray createAwtTray(TrayCallback callback)
    {
        return SwingUtil.runOnSwingThread(() -> {
            return createAwtTrayInternal(callback);
        });
    }

    private static @NonNull Tray createAwtTrayInternal(TrayCallback callback)
    {
        try
        {
            final SystemTray tray = SystemTray.getSystemTray();
            final Image scaledIcon = TrayUtil.loadImage(tray.getTrayIconSize());
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
    public void setTooltip(String tooltip)
    {
        trayIcon.setToolTip(tooltip);
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
