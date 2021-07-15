package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.SystemTray;

public class NativeSystemTray implements Tray
{
    private static final Logger LOG = LogManager.getLogger(NativeSystemTray.class);

    private final SystemTray tray;

    private NativeSystemTray(SystemTray systemTray)
    {
        this.tray = systemTray;
    }

    static boolean isNativeSystemTraySupported()
    {
        return getNativeSystemTray() != null;
    }

    private static SystemTray getNativeSystemTray()
    {
        SystemTray.DEBUG = LOG.isTraceEnabled();
        return SystemTray.get();
    }

    static NativeSystemTray create(TrayCallback callback)
    {
        final SystemTray systemTray = getNativeSystemTray();
        if (systemTray == null)
        {
            throw new IllegalStateException("Unable to load SystemTray");
        }
        systemTray.installShutdownHook();
        setIcon(systemTray);
        populateMenuMenu(systemTray.getMenu(), callback);
        systemTray.getMenu().setCallback(event -> callback.showMainWindow());
        return new NativeSystemTray(systemTray);
    }

    private static void setIcon(final SystemTray systemTray)
    {
        final int trayImageSize = systemTray.getTrayImageSize();
        final Dimension size = new Dimension(trayImageSize, trayImageSize);
        final Image image = TrayUtil.loadImage(size);
        systemTray.setImage(image);
    }

    private static void populateMenuMenu(Menu menu, TrayCallback callback)
    {
        menu.add(menuItem("Show", KeyEvent.VK_S, callback::showMainWindow));
        menu.add(menuItem("Interruption", KeyEvent.VK_I, callback::startInterruption));
        menu.add(new JSeparator());
        menu.add(menuItem("Exit", KeyEvent.VK_E, callback::exit));
    }

    private static JMenuItem menuItem(String label, int shortcutKey, Runnable action)
    {
        final JMenuItem item = new JMenuItem(label);
        item.setMnemonic(shortcutKey);
        item.addActionListener(event -> action.run());
        return item;
    }

    @Override
    public void removeTrayIcon()
    {
        tray.shutdown();
    }

    @Override
    public void setTooltip(String tooltip)
    {
        tray.setTooltip(tooltip);
    }

    @Override
    public boolean isSupported()
    {
        return true;
    }
}
