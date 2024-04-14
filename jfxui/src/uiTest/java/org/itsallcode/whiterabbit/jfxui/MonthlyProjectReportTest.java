package org.itsallcode.whiterabbit.jfxui;

import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.Locale;

import org.itsallcode.whiterabbit.api.model.Project;
import org.itsallcode.whiterabbit.jfxui.testutil.JunitTags;
import org.itsallcode.whiterabbit.jfxui.testutil.model.MonthlyProjectReportWindow;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class MonthlyProjectReportTest extends JavaFxAppUiTestBase
{
    private static final Instant INITIAL_TIME = Instant.parse("2007-12-03T10:15:30.20Z");
    private static final ProjectImpl PROJECT1 = project("p1", "Project 1");
    private static final ProjectImpl PROJECT2 = project("p2", "Project 2");

    FxRobot robot;

    @Test
    void emptyProjectReport()
    {
        time().tickSeparateMinutes(2);
        final MonthlyProjectReportWindow report = app().openMonthlyProjectReport(YearMonth.of(2007, 12));
        report.assertRowCount(0);

        report.closeViaCloseButton();
    }

    @Test
    void closeReportByTypingEscKey()
    {
        time().tickSeparateMinutes(2);
        final MonthlyProjectReportWindow report = app().openMonthlyProjectReport(YearMonth.of(2007, 12));
        report.closeViaEscKey();
    }

    @Test
    void filledProjectReport()
    {
        time().tickSeparateMinutes(3);

        final Project project = new ProjectImpl("p1", "Project 1", null);
        app().activitiesTable().addRemainderActivity(project, "a1");

        final MonthlyProjectReportWindow report = app().openMonthlyProjectReport(YearMonth.of(2007, 12));
        report.assertRowCount(1)
                .assertProject(0, PROJECT1, Duration.ofMinutes(2), "a1");
        report.closeViaCloseButton();
    }

    @Test
    void gotoPreviousMonth()
    {
        time().tickSeparateMinutes(3);

        final Project project = new ProjectImpl("p1", "Project 1", null);
        app().activitiesTable().addRemainderActivity(project, "a1");

        final MonthlyProjectReportWindow report = app().openMonthlyProjectReport(YearMonth.of(2007, 12));
        report.assertRowCount(1);
        report.gotoPreviousMonth();
        report.assertWindowTitle("Monthly Project Report 2007-11");
        report.assertRowCount(0);
        report.closeViaCloseButton();
    }

    @Test
    @Tag(JunitTags.FLAKY) // Second month change test is not working
    void gotoNextMonth()
    {
        time().tickSeparateMinutes(3);

        final Project project = new ProjectImpl("p1", "Project 1", null);
        app().activitiesTable().addRemainderActivity(project, "a1");

        final MonthlyProjectReportWindow report = app().openMonthlyProjectReport(YearMonth.of(2007, 12));
        report.assertRowCount(1);
        report.gotoNextMonth();
        report.assertWindowTitle("Monthly Project Report 2008-01");
        report.assertRowCount(0);
        report.closeViaCloseButton();
    }

    @Override
    @Start
    void start(final Stage stage)
    {
        setLocale(Locale.GERMANY);
        setInitialTime(INITIAL_TIME);
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
