package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow;
import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow.Builder;
import org.itsallcode.whiterabbit.jfxui.testutil.model.ActivitiesTable;
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
        assertThat(app().activitiesTable().getAddActivityButton().isDisabled()).isTrue();
    }

    @Test
    void addActivityButtonEnabledWhenDaySelected()
    {
        selectCurrentDay();
        assertThat(app().activitiesTable().getAddActivityButton().isDisabled()).isFalse();
    }

    @Test
    void activityTableEmptyByDefault()
    {
        selectCurrentDay();
        time().tickMinute();
        app().activitiesTable().table().assertRowCount(0);
    }

    @Test
    void clickingAddButtonAddsActivity()
    {
        selectCurrentDay();
        time().tickMinute();
        final ActivitiesTable activities = app().activitiesTable();

        activities.addActivity();

        activities.table().assertRowCount(1);
    }

    @Test
    void addActivitySelectRemainder()
    {
        addActivity();

        final JavaFxTable<ActivityPropertyAdapter> activitiesTable = app().activitiesTable().table();
        robot.clickOn(activitiesTable.getTableCell(0, "remainder"));
        activitiesTable.assertRowContent(0, ActivitiesTableExpectedRow.defaultRow().withRemainder(true).build());
    }

    @Test
    void addActivitySelectProject()
    {
        addActivity();

        final JavaFxTable<ActivityPropertyAdapter> activitiesTable = app().activitiesTable().table();
        final Node projectCell = activitiesTable.getTableCell(0, "project");

        robot.doubleClickOn(projectCell).clickOn(projectCell).type(KeyCode.ENTER);
        activitiesTable.assertRowContent(0, ActivitiesTableExpectedRow.defaultRow().withProject(PROJECT1).build());
    }

    @Test
    void addActivityForOtherDay()
    {
        final int rowTomorrow = time().getCurrentDayRowIndex() + 1;
        final JavaFxTable<ActivityPropertyAdapter> table = app().activitiesTable().table();

        app().genericDayTable().clickRow(rowTomorrow);

        table.assertRowCount(0);

        addActivity();

        table.assertRowCount(1);
    }

    @Test
    void activitiesTableUpdatedWhenSwitchingDays()
    {
        final int row = time().getCurrentDayRowIndex();
        app().genericDayTable().clickRow(row + 1);

        final JavaFxTable<ActivityPropertyAdapter> table = app().activitiesTable().table();
        table.assertRowCount(0);

        addActivity();
        table.assertRowCount(1);

        app().genericDayTable().clickRow(row);
        table.assertRowCount(0);
    }

    private void addActivity()
    {
        time().tickMinute();
        final ActivitiesTable activities = app().activitiesTable();

        activities.addActivity();

        final Builder expectedRowContent = ActivitiesTableExpectedRow.defaultRow();

        assertAll(() -> activities.table().assertRowCount(1),
                () -> activities.table().assertRowContent(0, expectedRowContent.build()));
    }

    private void selectCurrentDay()
    {
        final JavaFxTable<DayRecordPropertyAdapter> dayTable = app().genericDayTable();

        final int dayRowIndex = time().getCurrentDayRowIndex();
        robot.clickOn(dayTable.getTableRow(dayRowIndex));
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
