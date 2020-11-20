package org.itsallcode.whiterabbit.jfxui;

import java.nio.file.Path;

import org.itsallcode.whiterabbit.jfxui.service.DesktopService;
import org.itsallcode.whiterabbit.jfxui.ui.VacationReportViewer;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReport;

import javafx.application.HostServices;
import javafx.application.Platform;

public class UiActions
{
    private final Config config;
    private final DesktopService desktopService;
    private final AppService appService;
    private final HostServices hostServices;

    private UiActions(Config config, DesktopService desktopService, AppService appService, HostServices hostServices)
    {
        this.config = config;
        this.desktopService = desktopService;
        this.appService = appService;
        this.hostServices = hostServices;
    }

    static UiActions create(Config config, AppService appService, HostServices hostServices)
    {
        return new UiActions(config, DesktopService.create(), appService, hostServices);
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
        new VacationReportViewer(vacationReport).show();
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
