package org.itsallcode.whiterabbit.jfxui.testutil.model;

import java.time.Duration;

import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.testfx.api.FxRobot;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;

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

    public void addRemainderActivity(String comment)
    {
        addRemainderActivity(null, comment);
    }

    public void addRemainderActivity(Project project, String comment)
    {
        final int rowIndex = addActivity();

        if (project != null)
        {
            final Node projectCell = table.getTableCell(rowIndex, "project");
            robot.doubleClickOn(projectCell).clickOn(projectCell).clickOn(project.getLabel());
        }
        robot.clickOn(table.getTableCell(rowIndex, "remainder"));
        robot.doubleClickOn(table.getTableCell(rowIndex, "comment")).write(comment).type(KeyCode.ENTER);
    }

    public void addActivity(Duration duration, String comment)
    {
        addActivity(null, duration, comment);
    }

    public void addActivity(Project project, Duration duration, String comment)
    {
        final int rowIndex = addActivity();

        if (project != null)
        {
            final Node projectCell = table.getTableCell(rowIndex, "project");
            robot.doubleClickOn(projectCell).clickOn(projectCell).clickOn(project.getLabel());
            robot.clickOn(table.getTableCell(rowIndex, "duration"));
        }

        robot.doubleClickOn(table.getTableCell(rowIndex, "duration"))
                .write("0:" + duration.toMinutes())
                .type(KeyCode.ENTER);
        robot.doubleClickOn(table.getTableCell(rowIndex, "comment")).write(comment).type(KeyCode.ENTER);
    }

    public void toggleRemainder(int rowIndex)
    {
        robot.clickOn(table.getTableCell(rowIndex, "remainder"));
    }

    public int addActivity()
    {
        final Button addActivityButton = getAddActivityButton();
        robot.clickOn(addActivityButton);
        return table.getRowCount() - 1;
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
