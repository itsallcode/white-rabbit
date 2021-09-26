package org.itsallcode.whiterabbit.jfxui;

import org.itsallcode.whiterabbit.jfxui.splashscreen.SplashScreenLoader;
import org.itsallcode.whiterabbit.jfxui.systemmenu.DesktopIntegration;

import javafx.application.Application;

public class App
{
    public static void main(String[] args)
    {
        System.setProperty("javafx.preloader", SplashScreenLoader.class.getName());
        System.setProperty("log4j.skipJansi", "false");
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

        DesktopIntegration.getInstance().register();
        Application.launch(JavaFxApp.class, args);
    }
}
