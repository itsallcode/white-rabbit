package org.itsallcode.whiterabbit.jfxui.testutil.model;

import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.stage.Window;

public class ProjectReportWindow
{
    private final FxRobot robot;
    private final Window window;

    public ProjectReportWindow(FxRobot robot, Window window)
    {
        this.robot = robot;
        this.window = window;
    }

    public void close()
    {
        robot.clickOn("#close-button");
        Assertions.assertThat(window).isNotShowing();
    }
}
