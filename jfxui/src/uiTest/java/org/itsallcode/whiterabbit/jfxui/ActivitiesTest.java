package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow;
import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow.Builder;
import org.itsallcode.whiterabbit.jfxui.testutil.model.JavaFxTable;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
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
        time().tickMinute();
        lookupActivitiesTable().assertRowCount(0);
    }

    @Test
    void clickingAddButtonAddsActivity()
    {
        selectCurrentDay();
        time().tickMinute();
        clickAddActivityButton();

        lookupActivitiesTable().assertRowCount(1);
    }

    @Test
    void addActivitySelectRemainder()
    {
        addActivity();

        final JavaFxTable<ActivityPropertyAdapter> activitiesTable = lookupActivitiesTable();
        robot.clickOn(activitiesTable.getTableCell(0, "remainder"));
        activitiesTable.assertRowContent(0, ActivitiesTableExpectedRow.defaultRow().withRemainder(true).build());
    }

    @Test
    void addActivitySelectProject()
    {
        addActivity();

        final JavaFxTable<ActivityPropertyAdapter> activitiesTable = lookupActivitiesTable();
        final Node projectCell = activitiesTable.getTableCell(0, "project");

        robot.doubleClickOn(projectCell).clickOn(projectCell).type(KeyCode.ENTER);
        activitiesTable.assertRowContent(0, ActivitiesTableExpectedRow.defaultRow().withProject(PROJECT1).build());
    }

    @Test
    void addActivityForOtherDay()
    {
        final int rowTomorrow = time().getCurrentDayRowIndex() + 1;
        app().genericDayTable().clickRow(rowTomorrow);

        app().activitiesTable().assertRowCount(0);

        addActivity();

        app().activitiesTable().assertRowCount(1);
    }

    @Test
    void activitiesTableUpdatedWhenSwitchingDays()
    {
        final int row = time().getCurrentDayRowIndex();
        app().genericDayTable().clickRow(row + 1);

        app().activitiesTable().assertRowCount(0);

        addActivity();
        app().activitiesTable().assertRowCount(1);

        app().genericDayTable().clickRow(row);
        app().activitiesTable().assertRowCount(0);
    }

    private void addActivity()
    {
        time().tickMinute();
        final JavaFxTable<ActivityPropertyAdapter> activitiesTable = lookupActivitiesTable();

        clickAddActivityButton();

        final Builder expectedRowContent = ActivitiesTableExpectedRow.defaultRow();

        assertAll(() -> activitiesTable.assertRowCount(1),
                () -> activitiesTable.assertRowContent(0, expectedRowContent.build()));
    }

    private void selectCurrentDay()
    {
        final JavaFxTable<DayRecordPropertyAdapter> dayTable = app().genericDayTable();

        final int dayRowIndex = time().getCurrentDayRowIndex();
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

    private JavaFxTable<ActivityPropertyAdapter> lookupActivitiesTable()
    {
        return app().activitiesTable();
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setInitialTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        doStart(stage, projectConfig(PROJECT1, PROJECT2));
        setRobot(robot);
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
