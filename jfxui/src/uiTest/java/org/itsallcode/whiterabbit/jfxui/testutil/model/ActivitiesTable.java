package org.itsallcode.whiterabbit.jfxui.testutil.model;

import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.testfx.api.FxRobot;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

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

    public void removeActivity()
    {
        final Button removeActivityButton = getRemoveActivityButton();
        robot.clickOn(removeActivityButton);
    }

    public Button getRemoveActivityButton()
    {
        return robot.lookup("#remove-activity-button").queryButton();
    }

    public TableCell<?, ?> getCommentCell(int rowIndex)
    {
        return table.getTableCell(rowIndex, "comment");
    }

    public JavaFxTable<ActivityPropertyAdapter> table()
    {
        return table;
    }
}
