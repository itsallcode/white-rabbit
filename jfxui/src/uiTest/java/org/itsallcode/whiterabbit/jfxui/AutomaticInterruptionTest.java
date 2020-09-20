package org.itsallcode.whiterabbit.jfxui;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.AutomaticInterruptionDialog;
import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class AutomaticInterruptionTest extends JavaFxAppUiTestBase
{
    FxRobot robot;
    private LocalTime interruptionStart;
    private LocalTime interruptionEnd;
    private static ExecutorService executorService;

    @BeforeAll
    static void createExecutorService()
    {
        executorService = Executors.newSingleThreadExecutor();
    }

    @AfterAll
    static void shutdownExecutorService()
    {
        executorService.shutdown();
        executorService = null;
    }

    @Test
    void skipInterruptionButtonDoesNotAddInterruption()
    {
        simulateInterruption(dialog -> dialog.clickSkipInterruption());

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO);
        dayTable.assertBeginAndEnd(currentDayRowIndex, interruptionStart, interruptionEnd);
    }

    @Test
    void addInterruptionButtonAddsInterruption()
    {
        simulateInterruption(dialog -> dialog.clickAddInterruption());

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        dayTable.assertInterruption(currentDayRowIndex, Duration.ofMinutes(5));
        dayTable.assertBeginAndEnd(currentDayRowIndex, interruptionStart, interruptionEnd);
    }

    @Disabled("Not implemented yet, see https://github.com/itsallcode/white-rabbit/issues/15")
    @Test
    void stopWorkingForTodayButtonDoesNotAddsInterruption()
    {
        simulateInterruption(dialog -> dialog.clickStopWorkForToday());

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO);
        dayTable.assertBeginAndEnd(currentDayRowIndex, interruptionStart, interruptionStart);
    }

    // Delete this when https://github.com/itsallcode/white-rabbit/issues/15 is
    // fixed.
    @Test
    void stopWorkingForTodayButtonWronglyUpdatesEnd()
    {
        simulateInterruption(dialog -> dialog.clickStopWorkForToday());

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        dayTable.assertInterruption(currentDayRowIndex, Duration.ZERO);
        dayTable.assertBeginAndEnd(currentDayRowIndex, interruptionStart, interruptionEnd);
    }

    private void simulateInterruption(Consumer<AutomaticInterruptionDialog> closeDialogAction)
    {
        time().tickMinute();

        interruptionStart = time().getCurrentTimeMinutes();
        executorService.submit(() -> {
            time().tickMinute(Duration.ofMinutes(5));
        });
        TestUtil.sleepLong();
        interruptionEnd = time().getCurrentTimeMinutes();

        final AutomaticInterruptionDialog interruptionDialog = app().assertAutomaticInterruption();

        interruptionDialog.assertLabel(interruptionStart, Duration.ofMinutes(5));
        closeDialogAction.accept(interruptionDialog);
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
