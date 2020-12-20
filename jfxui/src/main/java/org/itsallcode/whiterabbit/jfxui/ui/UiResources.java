package org.itsallcode.whiterabbit.jfxui.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.itsallcode.whiterabbit.jfxui.JavaFxApp;

import javafx.scene.image.Image;

public class UiResources
{
    public static final int GAP_PIXEL = 10;
    public static final Image APP_ICON = loadImage("/icon.png");

    private UiResources()
    {
        // Not instantiable
    }

    private static Image loadImage(String resource)
    {
        try (InputStream resourceStream = JavaFxApp.class.getResourceAsStream(resource))
        {
            return new Image(resourceStream);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error loading resource " + resource, e);
        }
    }
}
