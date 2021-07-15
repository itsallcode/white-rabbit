package org.itsallcode.whiterabbit.jfxui.tray;

class DummyTrayIcon implements Tray
{
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
