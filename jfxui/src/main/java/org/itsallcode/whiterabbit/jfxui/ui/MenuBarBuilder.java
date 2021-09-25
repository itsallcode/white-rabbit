package org.itsallcode.whiterabbit.jfxui.ui;

import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.UiActions;
import org.itsallcode.whiterabbit.jfxui.tray.OsCheck;
import org.itsallcode.whiterabbit.jfxui.tray.OsCheck.OSType;
import org.itsallcode.whiterabbit.logic.service.AppPropertiesService.AppProperties;
import org.itsallcode.whiterabbit.logic.service.AppService;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

class MenuBarBuilder
{
    private static final Logger LOG = LogManager.getLogger(MenuBarBuilder.class);
    private final UiActions actions;
    private final AppService appService;
    private final BooleanProperty stoppedWorkingForToday;
    private final Stage primaryStage;

    MenuBarBuilder(UiActions actions, Stage primaryStage, AppService appService,
            BooleanProperty stoppedWorkingForToday)
    {
        this.actions = actions;
        this.primaryStage = primaryStage;
        this.appService = appService;
        this.stoppedWorkingForToday = Objects.requireNonNull(stoppedWorkingForToday);
    }

    public MenuBar build()
    {
        LOG.info("Creating menu bar");
        final MenuBar menuBar = new MenuBar();
        final Menu menuFile = menu("_File", "menu_file");
        final Menu menuCalculations = menu("_Working hours", "menu_working_hours");
        final Menu menuReports = menu("_Reports", "menu_reports");
        final Menu menuPlugins = menu("_Plugins", "menu_plugins");
        final Menu menuHelp = menu("_Help", "menu_help");
        menuFile.getItems().addAll(
                menuItem("Edit config file", "menuitem_edit_config", actions::editConfigFile),
                menuItem("Edit project file", "menuitem_edit_project", actions::editProjectFile),
                separatorItem(),
                menuItem("Open data directory", "menuitem_open_datadir", actions::openDataDir),
                menuItem("Open log directory", "menuitem_open_logdir", actions::openLogDir),
                menuItem("Open plugin directory", "menuitem_open_plugindir", actions::openPluginDir),
                separatorItem(),
                menuItem("_Quit", "menuitem_quit", actions::exitApp));
        menuCalculations.getItems().addAll(
                menuItem("_Update", "menuitem_update", appService::updateNow),
                menuItem("Update overtime for _all months", "menuitem_overtime",
                        appService::updatePreviousMonthOvertimeField),
                separatorItem(),
                createStopWorkingForTodayMenuItem());
        menuReports.getItems()
                .addAll(menuItem("_Project report", "menuitem_project_report", actions::showProjectReport),
                        menuItem("_Vacation report", "menuitem_vacation_report", actions::showVacationReport));
        menuPlugins.getItems()
                .addAll(menuItem("_Plugin manager", "menuitem_pluginmanager", actions::showPluginManager));
        menuHelp.getItems().addAll(menuItem("_About", "menuitem_about", this::showAboutDialog));
        menuBar.getMenus().addAll(menuFile, menuCalculations, menuReports, menuPlugins, menuHelp);
        if (OsCheck.getOperatingSystemType() == OSType.MACOS)
        {
            menuBar.useSystemMenuBarProperty().set(true);
        }
        return menuBar;
    }

    private SeparatorMenuItem separatorItem()
    {
        return new SeparatorMenuItem();
    }

    private Menu menu(String label, String id)
    {
        final Menu menu = new Menu(label);
        menu.setId(id);
        return menu;
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

    private MenuItem menuItem(String label, String id, Runnable action)
    {
        return menuItem(label, id, event -> action.run());
    }

    private MenuItem menuItem(String label, String id, EventHandler<ActionEvent> action)
    {
        final MenuItem menuItem = new MenuItem(label);
        menuItem.setId(id);
        menuItem.setOnAction(action);
        return menuItem;
    }

    private void showAboutDialog()
    {
        final AppProperties appProperties = appService.getAppProperties();
        final Alert aboutDialog = new Alert(AlertType.INFORMATION);
        aboutDialog.initModality(Modality.NONE);
        aboutDialog.initOwner(primaryStage);
        aboutDialog.setTitle("About White Rabbit");
        aboutDialog.setHeaderText("About White Rabbit:");
        aboutDialog.setContentText("Version: " + appProperties.getVersion());
        final ButtonType close = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
        final ButtonType homepage = new ButtonType("Open Homepage", ButtonData.HELP);
        aboutDialog.getButtonTypes().setAll(close, homepage);
        final Optional<ButtonType> selectedButton = aboutDialog.showAndWait();
        selectedButton.filter(response -> response == homepage).ifPresent(buttonType -> actions.openHomepage());
    }
}
