package org.itsallcode.whiterabbit.jfxui.service;

import java.awt.Desktop;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface DesktopService
{
    static DesktopService create()
    {
        final Logger log = LogManager.getLogger(DesktopService.class);
        if (Desktop.isDesktopSupported())
        {
            log.info("Desktop is supported, use real desktop service");
            return new RealDesktopService(Desktop.getDesktop());
        }
        log.info("Desktop is not supported, use fake desktop service");
        return new FakeDesktopService();
    }

    void open(Path file);
}
