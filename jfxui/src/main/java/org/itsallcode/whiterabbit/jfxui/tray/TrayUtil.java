package org.itsallcode.whiterabbit.jfxui.tray;

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrayUtil
{
    private static final Logger LOG = LogManager.getLogger(TrayUtil.class);

    static Image loadImage(Dimension size)
    {
        return loadImage("/icon.png", size);
    }

    private static Image loadImage(final String resourceName, Dimension size)
    {
        final URL imageUrl = AwtTrayIcon.class.getResource(resourceName);
        try
        {
            final Image image = ImageIO.read(imageUrl);
            LOG.debug("Scaling icon {} to {}", imageUrl, size);
            return image.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error loading image " + resourceName, e);
        }
    }
}
