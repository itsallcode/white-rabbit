package org.itsallcode.whiterabbit.jfxui.splashscreen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private Stage splashScreen;

    @Override
    public void start(Stage stage) throws Exception
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
        final Image image = new Image(this.getClass().getResourceAsStream("/icon.png"));
        final ImageView splashScreenImage = new ImageView(image);
        splashScreenImage.setX(0);
        splashScreenImage.setY(0);
        splashScreenImage.setFitHeight(300);
        splashScreenImage.setFitWidth(300);
        splashScreenImage.setPreserveRatio(true);
        final Group root = new Group(splashScreenImage);
        return new Scene(root, 300, 300);
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification notification)
    {
        if (notification instanceof ProgressPreloaderNotification)
        {
            final ProgressPreloaderNotification progressNotification = (ProgressPreloaderNotification) notification;
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
    public boolean handleErrorNotification(ErrorNotification info)
    {
        splashScreen.hide();
        final String location = info.getLocation() != null ? info.getLocation() + "\n" : "";
        final String message = "Error during initialization: " + location + info.getDetails() + "\n" + info.getCause();
        LOG.error(message, info.getCause());
        final Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
        return false;
    }
}
