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
    void duplicateInterruptionDialog()
    {
        time().tickMinute();

        interruptionStart = time().getCurrentTimeMinutes();
        executorService.submit(() -> {
            time().tickMinute(Duration.ofMinutes(5));
            time().tickMinute(Duration.ofMinutes(10));
        });
        TestUtil.sleepLong();
        interruptionEnd = time().getCurrentTimeMinutes();

        final AutomaticInterruptionDialog interruptionDialog = app().assertAutomaticInterruption();

        interruptionDialog.assertLabel(interruptionStart, Duration.ofMinutes(5));
        ((Consumer<AutomaticInterruptionDialog>) dialog -> dialog.clickAddInterruption()).accept(interruptionDialog);

        TestUtil.sleepShort();
        app().assertNoAutomaticInterruption();

        assertDay(Duration.ofMinutes(5), interruptionStart, interruptionEnd);
    }

    @Test
    void skipInterruptionButtonDoesNotAddInterruption()
    {
        simulateInterruption(dialog -> dialog.clickSkipInterruption());

        assertDay(Duration.ZERO, interruptionStart, interruptionEnd);
    }

    @Test
    void addInterruptionButtonAddsInterruption()
    {
        simulateInterruption(dialog -> dialog.clickAddInterruption());

        assertDay(Duration.ofMinutes(5), interruptionStart, interruptionEnd);
    }

    @Test
    void stopWorkingForTodayButtonDoesNotAddInterruptionDoesNotUpdateEnd()
    {
        simulateInterruption(dialog -> dialog.clickStopWorkForToday());

        assertDay(Duration.ZERO, interruptionStart, interruptionStart);
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

        TestUtil.sleepShort();
    }

    private void assertDay(Duration expectedInterruption, LocalTime expectedBegin, LocalTime expectedEnd)
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        dayTable.row(currentDayRowIndex).assertInterruption(expectedInterruption);
        dayTable.row(currentDayRowIndex).assertBeginAndEnd(expectedBegin, expectedEnd);
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
