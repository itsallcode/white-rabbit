package org.itsallcode.whiterabbit.jfxui.service;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class FakeDesktopService implements DesktopService
{
    private static final Logger LOG = LogManager.getLogger(FakeDesktopService.class);

    @Override
    public void open(Path file)
    {
        LOG.warn("Running in headless mode, can't open file {}", file);
    }
}
