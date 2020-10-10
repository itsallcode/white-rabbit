package org.itsallcode.whiterabbit.jfxui.testutil.model;

import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.stage.Window;

public class AboutDialogWindow
{
    private final FxRobot robot;
    private final Window window;

    public AboutDialogWindow(FxRobot robot, Window window)
    {
        this.robot = robot;
        this.window = window;
    }

    public String getText()
    {
        final Labeled label = robot.from(window.getScene().getRoot()).lookup(".content").queryLabeled();
        return label.getText();
    }

    public void close()
    {
        clickButton("Close");
        Assertions.assertThat(window).isNotShowing();
    }

    private void clickButton(String query)
    {
        final Button button = robot.from(window.getScene().getRoot()).lookup(query).queryButton();
        robot.clickOn(button);
    }
}
