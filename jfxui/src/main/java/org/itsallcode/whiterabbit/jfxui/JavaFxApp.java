package org.itsallcode.whiterabbit.jfxui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JavaFxApp extends Application
{
    private static final Logger LOG = LogManager.getLogger(App.class);

    private AppService appService;

    @Override
    public void init() throws Exception
    {
        final FormatterService formatterService = new FormatterService(Locale.US);
        final Path configFile = Paths.get("time.properties").toAbsolutePath();
        LOG.info("Loading config from {}", configFile);
        final Config config = Config.read(configFile);
        this.appService = AppService.create(config, formatterService);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        LOG.info("Starting UI");
        final StackPane root = new StackPane(new Label("Hello World!"));

        final Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
