package org.itsallcode.whiterabbit.jfxui.service;

import java.awt.Desktop;
import java.nio.file.Path;

public interface DesktopService
{
    static DesktopService create()
    {
        if (Desktop.isDesktopSupported())
        {
            return new RealDesktopService(Desktop.getDesktop());
        }
        return new FakeDesktopService();
    }

    void open(Path file);
}
