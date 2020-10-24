package org.itsallcode.whiterabbit.jfxui;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class AddInterruptionTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void addInterruptionViaFirstPreset()
    {
        time().tickMinute();

        app().addPresetInterruption(Duration.ofMinutes(5));

        assertNewDuration(Duration.ofMinutes(5));
    }

    @Test
    void addInterruptionVia45MinPreset()
    {
        time().tickMinute();

        app().addPresetInterruption(Duration.ofMinutes(45));

        assertNewDuration(Duration.ofMinutes(45));
    }

    @Test
    void cancelDialog()
    {
        time().tickMinute();

        app().addInterruption().enterDuration("5").clickCancelButton();

        assertNewDuration(Duration.ZERO);
    }

    @Test
    void addCustomInterruption()
    {
        time().tickMinute();

        app().addInterruption().enterDuration("7").clickAddButton();

        assertNewDuration(Duration.ofMinutes(7));
    }

    @Test
    void addCustomInterruptionWithInvalidNumber()
    {
        time().tickMinute();

        app().addInterruption().enterDuration("not-a-number").clickAddButton();

        assertNewDuration(Duration.ZERO);
    }

    @Test
    void addCustomInterruptionViaSpinner()
    {
        time().tickMinute();

        app().addInterruption().clickSpinnerUp().clickSpinnerUp().clickAddButton();

        assertNewDuration(Duration.ofMinutes(10));
    }

    private void assertNewDuration(Duration expectedDuration)
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        dayTable.assertInterruption(currentDayRowIndex, expectedDuration);
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
