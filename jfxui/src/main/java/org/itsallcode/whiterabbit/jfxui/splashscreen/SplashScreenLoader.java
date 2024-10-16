package org.itsallcode.whiterabbit.jfxui.splashscreen;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.OtherInstanceAlreadyRunningException;
import org.itsallcode.whiterabbit.jfxui.splashscreen.ProgressPreloaderNotification.Type;

import javafx.application.Preloader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreenLoader extends Preloader
{
    private static final Logger LOG = LogManager.getLogger(SplashScreenLoader.class);

    private Stage splashScreen = null;

    @Override
    public void start(final Stage stage)
    {
        splashScreen = stage;
        splashScreen.initStyle(StageStyle.UNDECORATED);
        splashScreen.setAlwaysOnTop(true);
        splashScreen.setScene(createScene());
        splashScreen.centerOnScreen();
        splashScreen.show();
    }

    public Scene createScene()
    {
        final ImageView splashScreenImage = new ImageView(loadImage("/icon.png"));
        splashScreenImage.setX(0);
        splashScreenImage.setY(0);
        splashScreenImage.setFitHeight(300);
        splashScreenImage.setFitWidth(300);
        splashScreenImage.setPreserveRatio(true);
        final Group root = new Group(splashScreenImage);
        return new Scene(root, 300, 300);
    }

    private Image loadImage(final String resourceName)
    {
        try (InputStream iconStream = getClass().getResourceAsStream(resourceName))
        {
            return new Image(iconStream);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error loading icon from resource '" + resourceName + "'", e);
        }
    }

    @Override
    public void handleApplicationNotification(final PreloaderNotification notification)
    {
        if (notification instanceof final ProgressPreloaderNotification progressNotification)
        {
            LOG.debug("Preloader application notification: {}", progressNotification.getNotificationType());
            if (progressNotification.getNotificationType() == Type.STARTUP_FINISHED)
            {
                splashScreen.hide();
            }
        }
        else
        {
            throw new IllegalStateException(
                    "Got unexpected notification of type " + notification.getClass() + ": " + notification);
        }
    }

    @Override
    public boolean handleErrorNotification(final ErrorNotification info)
    {
        splashScreen.hide();
        final Alert alert = createAlert(info);
        alert.showAndWait();
        return false;
    }

    private static Alert createAlert(final ErrorNotification info)
    {
        final Throwable exception = info.getCause();
        if (exception instanceof OtherInstanceAlreadyRunningException)
        {
            final String message = "Another instance of WhiteRabbit is already running.\n\n" + exception.getMessage();
            return new Alert(AlertType.WARNING, message, ButtonType.OK);
        }
        final String location = info.getLocation() != null ? (info.getLocation() + "\n") : "";
        final String message = "Error during initialization: " + location + info.getDetails() + "\n" + exception;
        LOG.error(message, exception);
        return new Alert(AlertType.ERROR, message, ButtonType.OK);
    }
}
