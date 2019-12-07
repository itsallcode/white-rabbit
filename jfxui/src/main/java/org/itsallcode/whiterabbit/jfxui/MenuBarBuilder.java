package org.itsallcode.whiterabbit.jfxui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.service.AppService;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
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
    private final BooleanProperty stoppedWorkingForToday;

    MenuBarBuilder(JavaFxApp app, AppService appService, BooleanProperty stoppedWorkingForToday)
    {
        this.app = app;
        this.appService = appService;
        this.stoppedWorkingForToday = stoppedWorkingForToday;
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
                createStopWorkingForTodayMenuItem(), //
                new SeparatorMenuItem(), //
                menuItem("_Quit", app::exitApp) //
        );

        menuBar.getMenus().addAll(menuFile);
        return menuBar;
    }

    private MenuItem createStopWorkingForTodayMenuItem()
    {
        final MenuItem menuItem = new MenuItem();
        menuItem.setOnAction(event -> appService.toggleStopWorkForToday());
        menuItem.textProperty()
                .bind(Bindings.createStringBinding(
                        () -> this.stoppedWorkingForToday.get() ? "_Continue working" : "_Stop working for today",
                        this.stoppedWorkingForToday));
        return menuItem;
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
