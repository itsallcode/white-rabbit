package org.itsallcode.whiterabbit.jfxui;

import java.time.*;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.AutomaticInterruptionDialog;
import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.*;

import javafx.scene.control.Button;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class StopWorkingTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void stopWorkingButtonChangesLabel()
    {
        final Button startStopWorkButton = getStartStopButton();
        Assertions.assertThat(startStopWorkButton).hasText("Stop working for today");

        robot.clickOn(startStopWorkButton);
        Assertions.assertThat(startStopWorkButton).hasText("Continue working");

        robot.clickOn(startStopWorkButton);
        Assertions.assertThat(startStopWorkButton).hasText("Stop working for today");
    }

    @Test
    void stopWorkingButtonStopsUpdatingEndTime()
    {
        final Button startStopWorkButton = getStartStopButton();
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickSeparateMinutes(3);
        TestUtil.sleepShort();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(LocalTime.of(11, 16), LocalTime.of(11, 18));

        robot.clickOn(startStopWorkButton);

        time().tickSeparateMinutes(3);
        TestUtil.sleepShort();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(LocalTime.of(11, 16), LocalTime.of(11, 18));
    }

    @Test
    void continueWorkingButtonUpdatesEndTime()
    {
        final Button startStopWorkButton = getStartStopButton();
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickSeparateMinutes(3);
        TestUtil.sleepShort();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(LocalTime.of(11, 16), LocalTime.of(11, 18));

        robot.clickOn(startStopWorkButton);
        time().tickMinute();
        robot.clickOn(startStopWorkButton);

        time().tickSeparateMinutes(3);
        TestUtil.sleepShort();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(LocalTime.of(11, 16), LocalTime.of(11, 22));
    }

    @Test
    void continueWorkingButtonShowsInterruptionDialog_addInterruption()
    {
        final Button startStopWorkButton = getStartStopButton();
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickSeparateMinutes(3);
        TestUtil.sleepShort();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(LocalTime.of(11, 16), LocalTime.of(11, 18));

        robot.clickOn(startStopWorkButton);
        time().tickMinute(Duration.ofMinutes(10));
        robot.clickOn(startStopWorkButton);

        final AutomaticInterruptionDialog interruptionDialog = app().assertAutomaticInterruption();
        interruptionDialog.clickAddInterruption();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(LocalTime.of(11, 16), LocalTime.of(11, 28));
        dayTable.row(currentDayRowIndex).assertInterruption(Duration.ofMinutes(10));
    }

    @Test
    void continueWorkingButtonShowsInterruptionDialog_skipInterruption()
    {
        final Button startStopWorkButton = getStartStopButton();
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickSeparateMinutes(3);
        TestUtil.sleepShort();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(LocalTime.of(11, 16), LocalTime.of(11, 18));

        robot.clickOn(startStopWorkButton);
        time().tickMinute(Duration.ofMinutes(10));
        robot.clickOn(startStopWorkButton);

        final AutomaticInterruptionDialog interruptionDialog = app().assertAutomaticInterruption();
        interruptionDialog.clickSkipInterruption();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(LocalTime.of(11, 16), LocalTime.of(11, 28));
        dayTable.row(currentDayRowIndex).assertInterruption(Duration.ZERO);
    }

    private Button getStartStopButton()
    {
        return robot.lookup("#start-stop-working-button").queryButton();
    }

    @Override
    @Start
    void start(final Stage stage)
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
