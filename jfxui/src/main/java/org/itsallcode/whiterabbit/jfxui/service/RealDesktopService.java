package org.itsallcode.whiterabbit.jfxui.service;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.tray.SwingUtil;

class RealDesktopService implements DesktopService
{
    private static final Logger LOG = LogManager.getLogger(RealDesktopService.class);
    private final Desktop desktop;

    RealDesktopService(Desktop desktop)
    {
        this.desktop = desktop;
    }

    @Override
    public void open(Path file)
    {
        SwingUtil.invokeInAwtEventQueue(() -> {
            LOG.info("Opening file {} with default application", file);
            try
            {
                desktop.open(file.toFile());
            }
            catch (final IOException e)
            {
                throw new UncheckedIOException("Error opening file " + file, e);
            }
        });
    }
}