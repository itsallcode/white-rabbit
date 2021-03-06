package org.itsallcode.whiterabbit.jfxui.testutil.model;

import java.time.Duration;
import java.time.LocalTime;

import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.stage.Window;

public class AutomaticInterruptionDialog
{
    private final FxRobot robot;
    private final Window window;

    public AutomaticInterruptionDialog(FxRobot robot, Window window)
    {
        this.robot = robot;
        this.window = window;
    }

    public void assertLabel(LocalTime startTime, Duration interruption)
    {
        Assertions.assertThat(window).isShowing();
        final Labeled label = robot.from(window.getScene().getRoot()).lookup(".label").queryLabeled();
        Assertions.assertThat(label)
                .hasText("An interruption of " + interruption + " was detected beginning at " + startTime + ".");
    }

    public void clickAddInterruption()
    {
        clickButton("Add interruption");
        Assertions.assertThat(window).isNotShowing();
    }

    public void clickSkipInterruption()
    {
        clickButton("Skip interruption");
        Assertions.assertThat(window).isNotShowing();
    }

    public void clickStopWorkForToday()
    {
        clickButton("Stop working for today");

        Assertions.assertThat(window).isNotShowing();
    }

    private void clickButton(String query)
    {
        final Button button = robot.from(window.getScene().getRoot()).lookup(query).queryButton();
        robot.clickOn(button);
    }
}
