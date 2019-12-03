package org.itsallcode.whiterabbit.jfxui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.service.AppService;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class MenuBarBuilder
{
    private static final Logger LOG = LogManager.getLogger(MenuBarBuilder.class);

    private final JavaFxApp app;
    private final AppService appService;

    MenuBarBuilder(JavaFxApp app, AppService appService)
    {
        this.app = app;
        this.appService = appService;
    }

    public MenuBar build()
    {
        LOG.info("Creating menu bar");
        final MenuBar menuBar = new MenuBar();
        final Menu menuFile = new Menu("_File");

        menuFile.getItems().addAll( //
                menuItem("_Update", appService::updateNow), //
                menuItem("Update overtime for _all months", appService::updatePreviousMonthOvertimeField), //
                new SeparatorMenuItem(), //
                menuItem("_Quit", app::exitApp) //
        );

        menuBar.getMenus().addAll(menuFile);
        return menuBar;
    }

    private MenuItem menuItem(String label, Runnable action)
    {
        return menuItem(label, event -> action.run());
    }

    private MenuItem menuItem(String label, EventHandler<ActionEvent> action)
    {
        final MenuItem menuItem = new MenuItem(label);
        menuItem.setOnAction(action);
        return menuItem;
    }
}
