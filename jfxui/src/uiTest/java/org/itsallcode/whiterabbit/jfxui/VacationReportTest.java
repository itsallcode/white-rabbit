package org.itsallcode.whiterabbit.jfxui;

import java.time.Instant;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class VacationReportTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void generateReportViaButton()
    {
        robot.clickOn(robot.lookup("Vacation report").queryButton());
    }

    @Test
    void generateReportViaMenu()
    {
        robot.clickOn("Reports").clickOn("Vacation report");
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setInitialTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        doStart(stage);
        setRobot(robot);
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
