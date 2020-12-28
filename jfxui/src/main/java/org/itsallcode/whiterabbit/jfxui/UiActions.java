package org.itsallcode.whiterabbit.jfxui;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.service.DesktopService;
import org.itsallcode.whiterabbit.jfxui.ui.ProjectReportViewer;
import org.itsallcode.whiterabbit.jfxui.ui.VacationReportViewer;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport;
import org.itsallcode.whiterabbit.logic.service.AppService;

import javafx.application.HostServices;
import javafx.application.Platform;
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
        openFileWithDefaultProgram(config.getProjectFile());
    }

    public void openDataDir()
    {
        openFileWithDefaultProgram(config.getDataDir());
    }

    private void openFileWithDefaultProgram(Path file)
    {
        desktopService.open(file);
    }

    public void showVacationReport()
    {
        final VacationReport vacationReport = appService.getVacationReport();
        new VacationReportViewer(getPrimaryStage(), vacationReport).show();
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
        new ProjectReportViewer(getPrimaryStage(), appService.formatter(), report).show();
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
