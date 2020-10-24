package org.itsallcode.whiterabbit.jfxui.testutil.model;

import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.stage.Window;

public class AddInterruptionDialog
{
    private final FxRobot robot;
    private final Window window;

    public AddInterruptionDialog(FxRobot robot, Window window)
    {
        this.robot = robot;
        this.window = window;
    }

    public void clickAddButton()
    {
        clickButton("Add interruption");
        Assertions.assertThat(window).isNotShowing();
    }

    public void clickCancelButton()
    {
        clickButton("Cancel");
        Assertions.assertThat(window).isNotShowing();
    }

    public AddInterruptionDialog enterDuration(String text)
    {
        final Spinner<?> spinner = (Spinner<?>) robot.from(window.getScene().getRoot())
                .lookup("#interruption-duration-spinner").query();
        robot.clickOn(spinner);
        robot.write(text);
        return this;
    }

    public AddInterruptionDialog clickSpinnerUp()
    {
        final Spinner<?> spinner = (Spinner<?>) robot.from(window.getScene().getRoot())
                .lookup("#interruption-duration-spinner").query();
        final Node spinnerUp = robot.from(spinner).lookup(".increment-arrow-button").query();
        robot.clickOn(spinnerUp);
        return this;
    }

    private void clickButton(String query)
    {
        final Button button = robot.from(window.getScene().getRoot()).lookup(query).queryButton();
        robot.clickOn(button);
    }
}
