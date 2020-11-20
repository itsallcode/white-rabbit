package org.itsallcode.whiterabbit.jfxui.service;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

class RealDesktopService implements DesktopService
{
    private final Desktop desktop;

    RealDesktopService(Desktop desktop)
    {
        this.desktop = desktop;
    }

    @Override
    public void open(Path file)
    {
        try
        {
            desktop.open(file.toFile());
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error opening file " + file, e);
        }
    }
}