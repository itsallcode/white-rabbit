package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.TrayIcon.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class DummyTrayIcon implements Tray
{
    private static final Logger LOG = LogManager.getLogger(DummyTrayIcon.class);

    @Override
    public void displayMessage(String caption, String text, MessageType messageType)
    {
        LOG.info("Display message with caption {} and text {} of type {}", caption, text, messageType);
    }

    @Override
    public void removeTrayIcon()
    {
        // nothing to do
    }

    @Override
    public boolean isSupported()
    {
        return false;
    }

    @Override
    public void setTooltip(String tooltip)
    {
        // nothing to do
    }
}
