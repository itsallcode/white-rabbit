package org.itsallcode.whiterabbit.jfxui.testutil.model;

import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

public class AboutDialogWindow
{
    private final FxRobot robot;
    private final Window window;

    public AboutDialogWindow(final FxRobot robot, final Window window)
    {
        this.robot = robot;
        this.window = window;
    }

    public String getHeaderText()
    {
        final GridPane pane = robot.from(window.getScene().getRoot()).lookup(".header-panel").queryAs(GridPane.class);
        final Label headerLabel = (Label) pane.getChildren().get(0);
        return headerLabel.getText();
    }

    public String getContentText()
    {
        final Labeled label = robot.from(window.getScene().getRoot()).lookup(".content").queryLabeled();
        return label.getText();
    }

    public void close()
    {
        clickButton("Close");
        Assertions.assertThat(window).isNotShowing();
    }

    private void clickButton(final String query)
    {
        final Button button = robot.from(window.getScene().getRoot()).lookup(query).queryButton();
        robot.clickOn(button);
    }
}
