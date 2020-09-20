package org.itsallcode.whiterabbit.jfxui;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.ApplicationHelper;
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
        final int currentDayRowIndex = getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        tickMinute();
        tickMinute();

        TestUtil.sleepShort();

        dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO);
    }

    @Test
    void interruptionDialogUpdatesLabelAfterTick()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        tickMinute();
        TestUtil.sleepShort();
        final InterruptionDialog interruptionDialog = new ApplicationHelper(robot).startInterruption();

        final LocalTime interruptionStartTime = getCurrentTimeSeconds();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime, Duration.ZERO));

        tickMinute();
        TestUtil.sleepShort();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime.plusMinutes(1), Duration.ofMinutes(1)),
                () -> dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO));
    }

    @Test
    void interruptionAddedWhenClickingAddButton()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        tickMinute();
        TestUtil.sleepShort();
        final InterruptionDialog interruptionDialog = new ApplicationHelper(robot).startInterruption();

        final LocalTime interruptionStartTime = getCurrentTimeSeconds();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime, Duration.ZERO));

        tickMinute();
        TestUtil.sleepShort();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime.plusMinutes(1), Duration.ofMinutes(1)),
                () -> dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO));

        interruptionDialog.clickAddInterruption();

        dayTable.assertInterruption(currentDayRowIndex, Duration.ofMinutes(1));
    }

    @Test
    void interruptionSkippedWhenClickingCancleButton()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        tickMinute();
        TestUtil.sleepShort();
        final InterruptionDialog interruptionDialog = new ApplicationHelper(robot).startInterruption();

        final LocalTime interruptionStartTime = getCurrentTimeSeconds();

        assertAll(() -> interruptionDialog.assertLabel(interruptionStartTime),
                () -> interruptionDialog.assertContent(interruptionStartTime, Duration.ZERO));

        tickMinute();
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
        setCurrentTime(Instant.parse("2007-12-03T10:15:30.20Z"));
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
