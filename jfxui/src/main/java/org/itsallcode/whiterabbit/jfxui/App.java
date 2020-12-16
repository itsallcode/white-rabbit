package org.itsallcode.whiterabbit.jfxui;

import javafx.application.Application;
import org.itsallcode.whiterabbit.jfxui.splashscreen.SplashScreenLoader;

public class App
{
    public static void main(String[] args)
    {
        System.setProperty("javafx.preloader", SplashScreenLoader.class.getName());
        System.setProperty("log4j.skipJansi", "false");
        Application.launch(JavaFxApp.class, args);
    }
}
