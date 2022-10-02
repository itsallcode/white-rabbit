package org.itsallcode.whiterabbit.jfxui;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.*;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.itsallcode.whiterabbit.jfxui.testutil.model.InterruptionDialog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.*;

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

        dayTable.row(currentDayRowIndex).assertInterruption(Duration.ZERO);
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
                () -> dayTable.row(currentDayRowIndex).assertInterruption(Duration.ZERO));
    }

    @Test
    void interruptionAddedWhenClickingAddButton()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        final LocalTime dayBeginTime = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();
        final InterruptionDialog interruptionDialog = app().startInterruption();

        final LocalTime interruptionStartTime = time().getCurrentTimeSeconds();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime, Duration.ZERO));

        time().tickMinute();
        final LocalTime dayEndTime = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime.plusMinutes(1), Duration.ofMinutes(1)),
                () -> dayTable.row(currentDayRowIndex).assertInterruption(Duration.ZERO),
                () -> dayTable.row(currentDayRowIndex).assertBeginAndEnd(dayBeginTime, dayEndTime));

        interruptionDialog.clickAddInterruption();

        dayTable.row(currentDayRowIndex).assertInterruption(Duration.ofMinutes(1));
    }

    @Test
    void endTimeUpdatedWhenAutomaticInterruptionDetected()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        final LocalTime dayBeginTime = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();
        final InterruptionDialog interruptionDialog = app().startInterruption();

        final Duration sleepTime = Duration.ofMinutes(5);
        time().addTime(sleepTime);

        final LocalTime dayEndTime = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();

        interruptionDialog.clickAddInterruption();

        assertAll(() -> dayTable.row(currentDayRowIndex).assertInterruption(sleepTime),
                () -> dayTable.row(currentDayRowIndex).assertBeginAndEnd(dayBeginTime, dayEndTime));
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
                () -> dayTable.row(currentDayRowIndex).assertInterruption(Duration.ZERO));

        interruptionDialog.clickCancelInterruption();

        dayTable.row(currentDayRowIndex).assertInterruption(Duration.ZERO);
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
