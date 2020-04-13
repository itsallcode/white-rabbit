package org.itsallcode.whiterabbit.jfxui.splashscreen;

import org.itsallcode.whiterabbit.jfxui.JavaFxApp;

import javafx.application.Preloader.PreloaderNotification;

public class ProgressPreloaderNotification implements PreloaderNotification
{
    public enum Type
    {
        BEFORE_LOAD, BEFORE_INIT, BEFORE_START, AFTER_START, AFTER_INIT
    }

    private final Type notificationType;
    private final JavaFxApp application;

    public ProgressPreloaderNotification(JavaFxApp application, Type notificationType)
    {
        this.application = application;
        this.notificationType = notificationType;
    }

    public Type getNotificationType()
    {
        return notificationType;
    }

    public JavaFxApp getApplication()
    {
        return application;
    }
}
