package org.itsallcode.whiterabbit.jfxui;

import org.itsallcode.whiterabbit.jfxui.splashscreen.SplashScreenLoader;

import javafx.application.Application;

public class App
{
    public static void main(String[] args)
    {
        System.setProperty("javafx.preloader", SplashScreenLoader.class.getName());
        Application.launch(JavaFxApp.class, args);
    }
}
