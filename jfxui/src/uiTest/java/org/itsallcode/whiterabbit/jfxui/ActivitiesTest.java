package org.itsallcode.whiterabbit.jfxui;

import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow;
import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow.Builder;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class ActivitiesTest extends JavaFxAppUiTestBase
{
    private static final Project PROJECT1 = project("p1", "Project 1");
    private static final Project PROJECT2 = project("p2", "Project 2");

    FxRobot robot;

    @Test
    void addActivitiesSelectRemainder()
    {
        addActivity();

        final TableView<?> activitiesTable = robot.lookup("#activities-table").queryTableView();
        robot.clickOn(getTableCell(activitiesTable, 0, "remainder"));
        assertRowContent(activitiesTable, 0, ActivitiesTableExpectedRow.defaultRow().withRemainder(true).build());
    }

    @Test
    void addActivitiesSelectProject()
    {
        addActivity();

        final TableView<?> activitiesTable = robot.lookup("#activities-table").queryTableView();
        final Node projectCell = getTableCell(activitiesTable, 0, "project");

        robot.doubleClickOn(projectCell).clickOn(projectCell).type(KeyCode.ENTER);
        assertRowContent(activitiesTable, 0, ActivitiesTableExpectedRow.defaultRow().withProject(PROJECT1).build());
    }

    private void addActivity()
    {
        tickMinute();
        final TableView<?> dayTable = robot.lookup("#day-table").queryTableView();
        final TableView<?> activitiesTable = robot.lookup("#activities-table").queryTableView();

        Assertions.assertThat(activitiesTable).hasExactlyNumRows(0);

        final int dayRowIndex = getCurrentDayRowIndex();
        robot.clickOn(getTableRow(dayTable, dayRowIndex));

        final Button addActivityButton = robot.lookup("#add-activity-button").queryButton();
        robot.clickOn(addActivityButton);

        Assertions.assertThat(activitiesTable).hasExactlyNumRows(1);
        final Builder expectedRowContent = ActivitiesTableExpectedRow.defaultRow();
        assertRowContent(activitiesTable, 0, expectedRowContent.build());
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setCurrentTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        doStart(stage, projectConfig(PROJECT1, PROJECT2));
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
