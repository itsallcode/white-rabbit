package org.itsallcode.whiterabbit.jfxui.testutil.model;

import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.testfx.api.FxRobot;

import javafx.scene.control.Button;

public class ActivitiesTable
{
    private final JavaFxTable<ActivityPropertyAdapter> table;
    private final FxRobot robot;

    ActivitiesTable(JavaFxTable<ActivityPropertyAdapter> table, FxRobot robot)
    {
        this.table = table;
        this.robot = robot;
    }

    public Button getAddActivityButton()
    {
        return robot.lookup("#add-activity-button").queryButton();
    }

    public void addActivity()
    {
        final Button addActivityButton = getAddActivityButton();
        robot.clickOn(addActivityButton);
    }

    public JavaFxTable<ActivityPropertyAdapter> table()
    {
        return table;
    }
}
