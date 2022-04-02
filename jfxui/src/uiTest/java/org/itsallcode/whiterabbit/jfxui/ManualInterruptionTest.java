package org.itsallcode.whiterabbit.jfxui;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.itsallcode.whiterabbit.jfxui.testutil.model.InterruptionDialog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class ManualInterruptionTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void interruptionIsZeroByDefault()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        time().tickMinute();

        TestUtil.sleepShort();

        dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO);
    }

    @Test
    void interruptionDialogUpdatesLabelAfterTick()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        TestUtil.sleepShort();
        final InterruptionDialog interruptionDialog = app().startInterruption();

        final LocalTime interruptionStartTime = time().getCurrentTimeSeconds();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime, Duration.ZERO));

        time().tickMinute();
        TestUtil.sleepShort();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime.plusMinutes(1), Duration.ofMinutes(1)),
                () -> dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO));
    }

    @Test
    void interruptionAddedWhenClickingAddButton()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        LocalTime dayBeginTime = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();
        final InterruptionDialog interruptionDialog = app().startInterruption();

        final LocalTime interruptionStartTime = time().getCurrentTimeSeconds();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime, Duration.ZERO));

        time().tickMinute();
        LocalTime dayEndTime = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime.plusMinutes(1), Duration.ofMinutes(1)),
                () -> dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO),
                () -> dayTable.assertBeginAndEnd(currentDayRowIndex, dayBeginTime, dayEndTime));

        interruptionDialog.clickAddInterruption();

        dayTable.assertInterruption(currentDayRowIndex, Duration.ofMinutes(1));
    }

    @Test
    void endTimeUpdatedWhenAutomaticInterruptionDetected()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        LocalTime dayBeginTime = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();
        final InterruptionDialog interruptionDialog = app().startInterruption();

        Duration sleepTime = Duration.ofMinutes(5);
        time().addTime(sleepTime);

        LocalTime dayEndTime = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();

        interruptionDialog.clickAddInterruption();

        assertAll(() -> dayTable.assertInterruption(currentDayRowIndex, sleepTime),
                () -> dayTable.assertBeginAndEnd(currentDayRowIndex, dayBeginTime, dayEndTime));
    }

    @Test
    void interruptionSkippedWhenClickingCancleButton()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        TestUtil.sleepShort();
        final InterruptionDialog interruptionDialog = app().startInterruption();

        final LocalTime interruptionStartTime = time().getCurrentTimeSeconds();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime, Duration.ZERO));

        time().tickMinute();
        TestUtil.sleepShort();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime.plusMinutes(1), Duration.ofMinutes(1)),
                () -> dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO));

        interruptionDialog.clickCancelInterruption();

        dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO);
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
