package org.itsallcode.whiterabbit.jfxui.testutil.model;

import java.time.Duration;
import java.time.LocalTime;

import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.stage.Window;

public class InterruptionDialog
{
    private final FxRobot robot;
    private final Window window;

    public InterruptionDialog(FxRobot robot, Window window)
    {
        this.robot = robot;
        this.window = window;
    }

    public void assertLabel(LocalTime startTime)
    {
        Assertions.assertThat(window).isShowing();
        final Labeled label = robot.from(window.getScene().getRoot()).lookup(".label").queryLabeled();
        Assertions.assertThat(label).hasText("Interruption started at " + startTime + ". End interruption now?");
    }

    public void assertContent(LocalTime currentTime, Duration interruption)
    {
        Assertions.assertThat(window).isShowing();
        final Labeled label = robot.from(window.getScene().getRoot()).lookup(".content").queryLabeled();
        Assertions.assertThat(label)
                .hasText("Current time: " + currentTime + ". Add interruption of " + interruption + "?");
    }

    public void clickAddInterruption()
    {
        final Button addInterruptionButton = robot.from(window.getScene().getRoot()).lookup("Add interruption")
                .queryButton();
        robot.clickOn(addInterruptionButton);

        Assertions.assertThat(window).isNotShowing();
    }

    public void clickCancelInterruption()
    {
        final Button cancelInterruptionButton = robot.from(window.getScene().getRoot()).lookup("Cancel interruption")
                .queryButton();
        robot.clickOn(cancelInterruptionButton);

        Assertions.assertThat(window).isNotShowing();
    }
}
