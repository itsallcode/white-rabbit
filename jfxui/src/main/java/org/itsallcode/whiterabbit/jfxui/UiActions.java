package org.itsallcode.whiterabbit.jfxui;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.jfxui.service.DesktopService;
import org.itsallcode.whiterabbit.jfxui.ui.DailyProjectReportViewer;
import org.itsallcode.whiterabbit.jfxui.ui.MonthlyProjectReportViewer;
import org.itsallcode.whiterabbit.jfxui.ui.PluginManagerViewer;
import org.itsallcode.whiterabbit.jfxui.ui.VacationReportViewer;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport;
import org.itsallcode.whiterabbit.logic.service.AppPropertiesService.AppProperties;
import org.itsallcode.whiterabbit.logic.service.AppService;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class UiActions
{
    private static final Logger LOG = LogManager.getLogger(UiActions.class);

    private final Config config;
    private final AppState state;
    private final DesktopService desktopService;
    private final AppService appService;
    private final HostServices hostServices;

    private UiActions(final Config config, final AppState state, final DesktopService desktopService,
            final AppService appService,
            final HostServices hostServices)
    {
        this.config = config;
        this.state = state;
        this.desktopService = desktopService;
        this.appService = appService;
        this.hostServices = hostServices;
    }

    static UiActions create(final Config config, final AppState state, final AppService appService,
            final HostServices hostServices)
    {
        return new UiActions(config, state, DesktopService.create(), appService, hostServices);
    }

    public void editConfigFile()
    {
        openFileWithDefaultProgram(config.getConfigFile());
    }

    public void editProjectFile()
    {
        openFileWithDefaultProgram(config.getProjectFile());
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

    private void createAndOpenDirectory(final Path directory)
    {
        if (!Files.exists(directory))
        {
            LOG.info("Directory {} does not exist: create it", directory);
            createDir(directory);
        }
        openFileWithDefaultProgram(directory);
    }

    private static void createDir(final Path directory)
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

    private void openFileWithDefaultProgram(final Path file)
    {
        desktopService.open(file);
    }

    public void showVacationReport()
    {
        final VacationReport vacationReport = appService.getVacationReport();
        new VacationReportViewer(getPrimaryStage(), state.uiState, vacationReport).show();
    }

    public void showDailyProjectReport()
    {
        final MonthIndex monthIndex = state.currentMonth.get();
        if (monthIndex == null)
        {
            LOG.warn("No month selected, can't generate project report");
            return;
        }
        final ProjectReport report = appService.generateProjectReport(monthIndex.getYearMonth());
        new DailyProjectReportViewer(getPrimaryStage(), state.uiState, appService, this, report).show();
    }

    public void showMonthlyProjectReport()
    {
        final MonthIndex monthIndex = state.currentMonth.get();
        if (monthIndex == null)
        {
            LOG.warn("No month selected, can't generate project report");
            return;
        }
        new MonthlyProjectReportViewer(getPrimaryStage(), state.uiState, appService, monthIndex.getYearMonth()).show();
    }

    public void showPluginManager()
    {
        new PluginManagerViewer(getPrimaryStage(), state.uiState, appService.pluginManager()).show();
    }

    public void showErrorDialog(final String message)
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
        hostServices.showDocument("https://github.com/itsallcode/white-rabbit/blob/main/README.md");
    }

    public void exitApp()
    {
        Platform.exit();
    }

    public void showAboutDialog()
    {
        JavaFxUtil.runOnFxApplicationThread(() -> {
            final Alert aboutDialog = new Alert(AlertType.INFORMATION);
            aboutDialog.initModality(Modality.NONE);
            if (state.getPrimaryStage().isPresent())
            {
                aboutDialog.initOwner(state.getPrimaryStage().get());
            }
            aboutDialog.setTitle("About White Rabbit");
            aboutDialog.setHeaderText(formatAboutHeaderText());
            aboutDialog.setContentText(formatSystemPropertyValues(getProperties()));
            final ButtonType close = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
            final ButtonType homepage = new ButtonType("Open Homepage", ButtonData.HELP);
            aboutDialog.getButtonTypes().setAll(close, homepage);
            final Optional<ButtonType> selectedButton = aboutDialog.showAndWait();
            selectedButton.filter(response -> response == homepage).ifPresent(buttonType -> this.openHomepage());
        });
    }

    private String formatAboutHeaderText()
    {
        final AppProperties properties = appService.getAppProperties();
        return "White Rabbit version " + properties.getVersion() + " (" + properties.getBuildDate() + ")";
    }

    private static String formatSystemPropertyValues(final Map<String, String> properties)
    {
        return properties.entrySet().stream() //
                .map(entry -> entry.getKey() + ": " + System.getProperty(entry.getValue())) //
                .collect(joining("\n"));
    }

    private static Map<String, String> getProperties()
    {
        final Map<String, String> properties = new LinkedHashMap<>();
        properties.put("Java Vendor", "java.vendor");
        properties.put("Java Version", "java.version");
        properties.put("Java Home", "java.home");
        properties.put("Operating system", "os.name");
        properties.put("Operating system version", "os.version");
        properties.put("Operating system architecture", "os.arch");
        return properties;
    }
}
