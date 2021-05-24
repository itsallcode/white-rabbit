package org.itsallcode.whiterabbit.jfxui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.jfxui.service.DesktopService;
import org.itsallcode.whiterabbit.jfxui.ui.PluginManagerViewer;
import org.itsallcode.whiterabbit.jfxui.ui.ProjectReportViewer;
import org.itsallcode.whiterabbit.jfxui.ui.VacationReportViewer;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport;
import org.itsallcode.whiterabbit.logic.service.AppService;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public final class UiActions
{
    private static final Logger LOG = LogManager.getLogger(UiActions.class);

    private final Config config;
    private final AppState state;
    private final DesktopService desktopService;
    private final AppService appService;
    private final HostServices hostServices;

    private UiActions(Config config, AppState state, DesktopService desktopService, AppService appService,
            HostServices hostServices)
    {
        this.config = config;
        this.state = state;
        this.desktopService = desktopService;
        this.appService = appService;
        this.hostServices = hostServices;
    }

    static UiActions create(Config config, AppState state, AppService appService, HostServices hostServices)
    {
        return new UiActions(config, state, DesktopService.create(), appService, hostServices);
    }

    public void editConfigFile()
    {
        openFileWithDefaultProgram(config.getConfigFile());
    }

    public void editProjectFile()
    {
        openFileWithDefaultProgram(config.getProjectFileProvider().getProjectFile());
    }

    public void openDataDir()
    {
        createAndOpenDirectory(config.getDataDir());
    }

    public void openLogDir()
    {
        createAndOpenDirectory(config.getLogDir());
    }

    public void openPluginDir()
    {
        createAndOpenDirectory(config.getPluginDir());
    }

    private void createAndOpenDirectory(Path directory)
    {
        if (!Files.exists(directory))
        {
            LOG.info("Directory {} does not exist: create it", directory);
            createDir(directory);
        }
        openFileWithDefaultProgram(directory);
    }

    private void createDir(Path directory)
    {
        try
        {
            Files.createDirectories(directory);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error creating directory " + directory, e);
        }
    }

    private void openFileWithDefaultProgram(Path file)
    {
        desktopService.open(file);
    }

    public void showVacationReport()
    {
        final VacationReport vacationReport = appService.getVacationReport();
        new VacationReportViewer(getPrimaryStage(), state.uiState, vacationReport).show();
    }

    public void showProjectReport()
    {
        final MonthIndex monthIndex = state.currentMonth.get();
        if (monthIndex == null)
        {
            LOG.warn("No month selected, can't generate project report");
            return;
        }
        final ProjectReport report = appService.generateProjectReport(monthIndex.getYearMonth());
        new ProjectReportViewer(getPrimaryStage(), state.uiState, appService, this, report).show();
    }

    public void showPluginManager()
    {
        new PluginManagerViewer(getPrimaryStage(), state.uiState, appService.pluginManager()).show();
    }

    public void showErrorDialog(String message)
    {
        JavaFxUtil.runOnFxApplicationThread(() -> {
            final Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
            alert.show();
        });
    }

    private Stage getPrimaryStage()
    {
        return state.getPrimaryStage().orElseThrow();
    }

    public void openHomepage()
    {
        hostServices.showDocument("https://github.com/itsallcode/white-rabbit/blob/develop/README.md");
    }

    public void exitApp()
    {
        Platform.exit();
    }
}
