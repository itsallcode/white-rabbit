package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow;
import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow.Builder;
import org.itsallcode.whiterabbit.jfxui.testutil.JavaFxTable;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class ActivitiesTest extends JavaFxAppUiTestBase
{
    private static final Project PROJECT1 = project("p1", "Project 1");
    private static final Project PROJECT2 = project("p2", "Project 2");

    FxRobot robot;

    @Disabled("Not implemented yet, see https://github.com/itsallcode/white-rabbit/issues/23")
    @Test
    void addActivityButtonDisabledWhenNoDaySelected()
    {
        assertThat(getAddActivityButton().isDisabled()).isTrue();
    }

    @Test
    void addActivityButtonEnabledWhenDaySelected()
    {
        selectCurrentDay();
        assertThat(getAddActivityButton().isDisabled()).isFalse();
    }

    @Test
    void activityTableEmptyByDefault()
    {
        selectCurrentDay();
        tickMinute();
        Assertions.assertThat(lookupActivitiesTable().table()).hasExactlyNumRows(0);
    }

    @Test
    void clickingAddButtonAddsActivity()
    {
        selectCurrentDay();
        tickMinute();
        clickAddActivityButton();

        Assertions.assertThat(lookupActivitiesTable().table()).hasExactlyNumRows(1);
    }

    @Test
    void addActivitySelectRemainder()
    {
        addActivity();

        final JavaFxTable activitiesTable = lookupActivitiesTable();
        robot.clickOn(activitiesTable.getTableCell(0, "remainder"));
        activitiesTable.assertRowContent(0, ActivitiesTableExpectedRow.defaultRow().withRemainder(true).build());
    }

    @Test
    void addActivitySelectProject()
    {
        addActivity();

        final JavaFxTable activitiesTable = lookupActivitiesTable();
        final Node projectCell = activitiesTable.getTableCell(0, "project");

        robot.doubleClickOn(projectCell).clickOn(projectCell).type(KeyCode.ENTER);
        activitiesTable.assertRowContent(0, ActivitiesTableExpectedRow.defaultRow().withProject(PROJECT1).build());
    }

    private void addActivity()
    {
        tickMinute();
        final JavaFxTable activitiesTable = lookupActivitiesTable();

        selectCurrentDay();

        clickAddActivityButton();

        final Builder expectedRowContent = ActivitiesTableExpectedRow.defaultRow();

        assertAll(() -> Assertions.assertThat(activitiesTable.table()).hasExactlyNumRows(1),
                () -> activitiesTable.assertRowContent(0, expectedRowContent.build()));
    }

    private void selectCurrentDay()
    {
        final JavaFxTable dayTable = JavaFxTable.findDayTable(robot);

        final int dayRowIndex = getCurrentDayRowIndex();
        robot.clickOn(dayTable.getTableRow(dayRowIndex));
    }

    private void clickAddActivityButton()
    {
        final Button addActivityButton = getAddActivityButton();
        robot.clickOn(addActivityButton);
    }

    private Button getAddActivityButton()
    {
        return robot.lookup("#add-activity-button").queryButton();
    }

    private JavaFxTable lookupActivitiesTable()
    {
        return JavaFxTable.findActivitiesTable(robot);
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
