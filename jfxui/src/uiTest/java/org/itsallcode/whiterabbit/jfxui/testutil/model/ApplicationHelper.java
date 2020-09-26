package org.itsallcode.whiterabbit.jfxui.testutil.model;

import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.Button;
import javafx.stage.Window;

public class ApplicationHelper
{
    private final FxRobot robot;

    public ApplicationHelper(FxRobot robot)
    {
        this.robot = robot;
    }

    public InterruptionDialog startInterruption()
    {
        final Button startInterruptionButton = robot.lookup("#start-interruption-button").queryButton();
        robot.clickOn(startInterruptionButton);
        final Window window = robot.window("Add interruption");
        return new InterruptionDialog(robot, window);
    }

    public JavaFxTable activitiesTable()
    {
        return JavaFxTable.find(robot, "#activities-table");
    }

    public JavaFxTable genericDayTable()
    {
        return JavaFxTable.find(robot, "#day-table");
    }

    public DayTable dayTable()
    {
        return new DayTable(genericDayTable(), robot);
    }

    public AutomaticInterruptionDialog assertAutomaticInterruption()
    {
        final Window dialogWindow = robot.window("Interruption detected");
        Assertions.assertThat(dialogWindow).isShowing();
        return new AutomaticInterruptionDialog(robot, dialogWindow);
    }
}
