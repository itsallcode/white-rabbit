package org.itsallcode.whiterabbit.jfxui;

import org.itsallcode.whiterabbit.jfxui.log.LoggingConfigurator;
import org.itsallcode.whiterabbit.jfxui.splashscreen.SplashScreenLoader;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.ConfigLoader;
import org.itsallcode.whiterabbit.logic.DefaultWorkingDirProvider;

import javafx.application.Application;

public class App
{
    public static void main(String[] args)
    {
        System.setProperty("javafx.preloader", SplashScreenLoader.class.getName());
        final ConfigLoader configLoader = new ConfigLoader(new DefaultWorkingDirProvider());
        final Config config = configLoader.loadConfigFromDefaultLocations();
        new LoggingConfigurator(config).configure();
        Application.launch(JavaFxApp.class, args);
    }
}
