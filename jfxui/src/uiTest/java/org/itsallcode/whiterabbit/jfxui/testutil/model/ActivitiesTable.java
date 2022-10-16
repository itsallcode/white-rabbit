package org.itsallcode.whiterabbit.jfxui.testutil.model;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.model.Project;
import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.testfx.api.FxRobot;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;

public class ActivitiesTable
{
    private static final Logger LOG = LogManager.getLogger(ActivitiesTable.class);
    private final JavaFxTable<ActivityPropertyAdapter> table;
    private final FxRobot robot;

    ActivitiesTable(final JavaFxTable<ActivityPropertyAdapter> table, final FxRobot robot)
    {
        this.table = table;
        this.robot = robot;
    }

    public Button getAddActivityButton()
    {
        return robot.lookup("#add-activity-button").queryButton();
    }

    public void addRemainderActivity(final String comment)
    {
        addRemainderActivity(null, comment);
    }

    public void addRemainderActivity(final Project project, final String comment)
    {
        final int rowIndex = addActivity();

        if (project != null)
        {
            final Node projectCell = table.row(rowIndex).cell("project");
            robot.doubleClickOn(projectCell).clickOn(projectCell).clickOn(project.getLabel());
        }
        final TableCell<?, ?> remainderCell = table.row(rowIndex).cell("remainder");
        if (remainderCell.getItem() != null && !(Boolean) remainderCell.getItem())
        {
            robot.clickOn(remainderCell);
        }
        robot.doubleClickOn(table.row(rowIndex).cell("comment")).write(comment).type(KeyCode.ENTER);
    }

    public void addActivity(final Duration duration, final String comment)
    {
        addActivity(null, duration, comment);
    }

    public void addActivity(final Project project, final Duration duration, final String comment)
    {
        final int rowIndex = addActivity();

        final TableCell<?, ?> remainderCell = table.row(rowIndex).cell("remainder");
        if ((Boolean) remainderCell.getItem())
        {
            LOG.debug("Remainder cell value is {}: click {}", remainderCell.getItem(), remainderCell);
            robot.clickOn(remainderCell);
        }
        else
        {
            LOG.debug("Remainder cell value is {}: don't click it", remainderCell.getItem());
        }

        if (project != null)
        {
            final Node projectCell = table.row(rowIndex).cell("project");
            LOG.debug("Select project {}", project.getLabel());
            robot.doubleClickOn(projectCell).clickOn(projectCell).clickOn(project.getLabel());
            LOG.debug("Click on duration cell");
            robot.clickOn(table.row(rowIndex).cell("duration"));
        }

        LOG.debug("Type duration of {} minutes", duration.toMinutes());
        robot.doubleClickOn(table.row(rowIndex).cell("duration"))
                .write("0:" + duration.toMinutes())
                .type(KeyCode.ENTER);
        robot.doubleClickOn(table.row(rowIndex).cell("comment")).write(comment).type(KeyCode.ENTER);
    }

    public void toggleRemainder(final int rowIndex)
    {
        robot.clickOn(table.row(rowIndex).cell("remainder"));
    }

    public int addActivity()
    {
        final Button addActivityButton = getAddActivityButton();
        LOG.debug("Clicking button {} to add an activity", addActivityButton);
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

    public TableCell<?, ?> getCommentCell(final int rowIndex)
    {
        return table.row(rowIndex).cell("comment");
    }

    public JavaFxTable<ActivityPropertyAdapter> table()
    {
        return table;
    }
}
