package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.SystemTray;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;

public class SystemTrayService
{
    private static final Logger LOG = LogManager.getLogger(SystemTrayService.class);

    private final Config config;

    public SystemTrayService(Config config)
    {
        this.config = config;
    }

    public Tray createTray(TrayCallback callback)
    {
        if (getTrayType() == TrayType.AWT && SystemTray.isSupported())
        {
            LOG.debug("User selected AWT tray");
            return AwtTrayIcon.createAwtTray(callback);
        }

        if (NativeSystemTray.isNativeSystemTraySupported())
        {
            LOG.debug("Using native system tray");
            return NativeSystemTray.create(callback);
        }

        if (AwtTrayIcon.isAwtTraySupported())
        {
            LOG.debug("Using AWT tray");
            return AwtTrayIcon.createAwtTray(callback);
        }

        LOG.debug("No tray supported");
        return new DummyTrayIcon();
    }

    private TrayType getTrayType()
    {
        return config.getOptionalValue("system.tray")
                .map(value -> value.equalsIgnoreCase("awt") ? TrayType.AWT : TrayType.NATIVE_SYSTEM)
                .orElse(TrayType.NATIVE_SYSTEM);
    }

    private enum TrayType
    {
        NATIVE_SYSTEM, AWT;
    }
}
