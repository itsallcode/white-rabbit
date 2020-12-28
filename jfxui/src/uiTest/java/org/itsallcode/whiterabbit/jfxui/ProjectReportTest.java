package org.itsallcode.whiterabbit.jfxui;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.model.ProjectReportWindow;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class ProjectReportTest extends JavaFxAppUiTestBase
{
    private static final Project PROJECT1 = project("p1", "Project 1");
    private static final Project PROJECT2 = project("p2", "Project 2");

    FxRobot robot;

    @Test
    void emptyProjectReport()
    {
        time().tickSeparateMinutes(2);
        final ProjectReportWindow report = app().openProjectReport();
        report.assertDayCount(31).assertProjectCount(0, 0);

        report.closeViaCloseButton();
    }

    @Test
    void closeReportByTypingEscKey()
    {
        time().tickSeparateMinutes(2);
        final ProjectReportWindow report = app().openProjectReport();
        report.closeViaEscKey();
    }

    @Test
    void filledProjectReport()
    {
        time().tickSeparateMinutes(3);

        final Project project = new Project("p1", "Project 1", null);
        app().activitiesTable().addRemainderActivity(project, "a1");

        final ProjectReportWindow report = app().openProjectReport();
        final int dayIndex = time().getCurrentDayRowIndex();
        report.assertDayCount(31)
                .assertProjectCount(dayIndex, 1)
                .assertProject(dayIndex, 0, PROJECT1, Duration.ofMinutes(2), "a1");
        report.closeViaCloseButton();
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setInitialTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        doStart(stage, projectConfig(PROJECT1, PROJECT2));
        setRobot(robot);
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
