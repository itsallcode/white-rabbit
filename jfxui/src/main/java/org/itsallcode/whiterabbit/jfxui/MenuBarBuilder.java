package org.itsallcode.whiterabbit.jfxui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuBarBuilder
{
    private static final Logger LOG = LogManager.getLogger(MenuBarBuilder.class);

    private final JavaFxApp app;

    MenuBarBuilder(JavaFxApp app)
    {
        this.app = app;
    }

    public MenuBar build()
    {
        LOG.info("Creating menu bar");
        final MenuBar menuBar = new MenuBar();
        final Menu menuFile = new Menu("File");

        final MenuItem quitMenueItem = new MenuItem("Quit");
        quitMenueItem.setOnAction(event -> app.exitApp());
        menuFile.getItems().addAll(quitMenueItem);

        menuBar.getMenus().addAll(menuFile);
        return menuBar;
    }
}
