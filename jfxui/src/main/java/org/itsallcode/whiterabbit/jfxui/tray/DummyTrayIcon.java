package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.TrayIcon.MessageType;

class DummyTrayIcon implements Tray
{

    @Override
    public void displayMessage(String caption, String text, MessageType messageType)
    {
    }
}
