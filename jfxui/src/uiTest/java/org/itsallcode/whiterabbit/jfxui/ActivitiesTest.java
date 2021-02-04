package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.api.model.Project;
import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow;
import org.itsallcode.whiterabbit.jfxui.testutil.ActivitiesTableExpectedRow.Builder;
import org.itsallcode.whiterabbit.jfxui.testutil.model.ActivitiesTable;
import org.itsallcode.whiterabbit.jfxui.testutil.model.JavaFxTable;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
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
    private static final ProjectImpl PROJECT1 = project("p1", "Project 1");
    private static final ProjectImpl PROJECT2 = project("p2", "Project 2");

    FxRobot robot;

    @Disabled("Not implemented yet, see https://github.com/itsallcode/white-rabbit/issues/23")
    @Test
    void addActivityButtonDisabledWhenNoDaySelected()
    {
        time().tickMinute();
        assertThat(app().activitiesTable().getAddActivityButton().isDisabled()).isTrue();
    }

    @Test
    void addActivityButtonEnabledWhenDaySelected()
    {
        time().tickMinute();
        selectCurrentDay();
        assertThat(app().activitiesTable().getAddActivityButton().isDisabled()).isFalse();
    }

    @Test
    void activityTableEmptyByDefault()
    {
        time().tickMinute();
        selectCurrentDay();
        app().activitiesTable().table().assertRowCount(0);
    }

    @Test
    void clickingAddButtonAddsActivity()
    {
        time().tickMinute();
        selectCurrentDay();
        final ActivitiesTable activities = app().activitiesTable();

        activities.addActivity();

        activities.table().assertRowCount(1);
    }

    @Disabled("Not implemented yet, see https://github.com/itsallcode/white-rabbit/issues/23")
    @Test
    void removeButtonDisabledWhenNoActivitySelected()
    {
        time().tickMinute();
        selectCurrentDay();
        final ActivitiesTable activities = app().activitiesTable();

        activities.addActivity();

        activities.table().assertRowCount(1);

        assertThat(activities.getRemoveActivityButton().isDisable()).isTrue();
    }

    @Test
    void clickRemoveButtonWhenNoActivitySelectedDoesNothing()
    {
        time().tickMinute();
        selectCurrentDay();
        final ActivitiesTable activities = app().activitiesTable();

        activities.addActivity();

        activities.table().assertRowCount(1);

        activities.removeActivity();

        activities.table().assertRowCount(1);
    }

    @Test
    void clickRemoveButtonRemovesSelectedActivity()
    {
        time().tickMinute();
        selectCurrentDay();
        final ActivitiesTable activities = app().activitiesTable();

        activities.addActivity();

        activities.table().assertRowCount(1);

        activities.table().clickRow(0);

        activities.removeActivity();

        activities.table().assertRowCount(0);
    }

    @Test
    void addRemainderActivityWithValues()
    {
        time().tickMinute();
        final ActivitiesTable activities = app().activitiesTable();

        final Project project = new ProjectImpl("p1", "Project 1", null);
        activities.addRemainderActivity(project, "tst");

        activities.table().assertContent(ActivitiesTableExpectedRow.defaultRow()
                .withProject(project)
                .withDuration(Duration.ZERO)
                .withRemainder(true)
                .withComment("tst")
                .build());
    }

    @Test
    void addFixedDurationActivityWithValues()
    {
        time().tickMinute();
        final ActivitiesTable activities = app().activitiesTable();

        final Project project = new ProjectImpl("p1", "Project 1", null);
        activities.addActivity(project, Duration.ofMinutes(5), "tst");

        activities.table().assertContent(ActivitiesTableExpectedRow.defaultRow()
                .withProject(project)
                .withDuration(Duration.ofMinutes(5))
                .withRemainder(false)
                .withComment("tst")
                .build());
    }

    @Test
    void remainderDurationCalculated()
    {
        time().tickSeparateMinutes(11);

        final ActivitiesTable activities = app().activitiesTable();

        activities.addRemainderActivity("a1");
        activities.addActivity(Duration.ofMinutes(7), "a2");

        final Builder activity1 = ActivitiesTableExpectedRow.defaultRow()
                .withDuration(Duration.ofMinutes(3))
                .withRemainder(true)
                .withComment("a1");
        final Builder activity2 = ActivitiesTableExpectedRow.defaultRow()
                .withDuration(Duration.ofMinutes(7))
                .withRemainder(false)
                .withComment("a2");

        activities.table().assertContent(activity1.build(), activity2.build());
    }

    @Test
    void remainderDurationUpdatedAtMinuteTick()
    {
        time().tickSeparateMinutes(11);

        final ActivitiesTable activities = app().activitiesTable();

        activities.addRemainderActivity("a1");
        activities.addActivity(Duration.ofMinutes(7), "a2");

        final Builder activity1 = ActivitiesTableExpectedRow.defaultRow()
                .withDuration(Duration.ofMinutes(3))
                .withRemainder(true)
                .withComment("a1");
        final Builder activity2 = ActivitiesTableExpectedRow.defaultRow()
                .withDuration(Duration.ofMinutes(7))
                .withRemainder(false)
                .withComment("a2");

        activities.table().assertContent(activity1.build(), activity2.build());

        time().tickMinute();

        activity1.withDuration(Duration.ofMinutes(4));
        activities.table().assertContent(activity1.build(), activity2.build());
    }

    @Test
    void toggleRemainderDurationUpdatedAtMinuteTick()
    {
        time().tickSeparateMinutes(11);

        final ActivitiesTable activities = app().activitiesTable();

        activities.addRemainderActivity("a1");
        activities.addActivity(Duration.ofMinutes(7), "a2");

        final Builder activity1 = ActivitiesTableExpectedRow.defaultRow()
                .withDuration(Duration.ofMinutes(3))
                .withRemainder(true)
                .withComment("a1");
        final Builder activity2 = ActivitiesTableExpectedRow.defaultRow()
                .withDuration(Duration.ofMinutes(7))
                .withRemainder(false)
                .withComment("a2");

        activities.table().assertContent(activity1.build(), activity2.build());

        activities.toggleRemainder(1);

        time().tickMinute();

        activity2.withRemainder(true).withDuration(Duration.ofMinutes(8));
        activity1.withRemainder(false);
        activities.table().assertContent(activity1.build(), activity2.build());
    }

    @Test
    void addActivityDeselectRemainder()
    {
        time().tickMinute();
        addActivity();

        final JavaFxTable<ActivityPropertyAdapter> activitiesTable = app().activitiesTable().table();
        robot.clickOn(activitiesTable.getTableCell(0, "remainder"));
        activitiesTable.assertRowContent(0, ActivitiesTableExpectedRow.defaultRow().withRemainder(false).build());
    }

    @Test
    void addActivitySelectProject()
    {
        time().tickMinute();
        addActivity();

        final JavaFxTable<ActivityPropertyAdapter> activitiesTable = app().activitiesTable().table();
        final Node projectCell = activitiesTable.getTableCell(0, "project");

        robot.doubleClickOn(projectCell).clickOn(projectCell).type(KeyCode.ENTER);
        activitiesTable.assertRowContent(0,
                ActivitiesTableExpectedRow.defaultRow().withRemainder(true).withProject(PROJECT1).build());
    }

    @Test
    void addActivityForOtherDay()
    {
        time().tickMinute();
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
        time().tickMinute();
        final int row = time().getCurrentDayRowIndex();
        app().genericDayTable().clickRow(row + 1);

        final JavaFxTable<ActivityPropertyAdapter> table = app().activitiesTable().table();
        table.assertRowCount(0);

        addActivity();
        table.assertRowCount(1);

        app().genericDayTable().clickRow(row);
        table.assertRowCount(0);
    }

    @Test
    void activitiesTableUpdatedWhenDayChanges()
    {
        time().tickMinute();

        final ActivitiesTable activities = app().activitiesTable();
        final JavaFxTable<ActivityPropertyAdapter> table = activities.table();
        table.assertRowCount(0);

        activities.addActivity();
        table.assertRowCount(1);

        time().tickDay();
        table.assertRowCount(0);
    }

    @Test
    void activitiesDurationUpdatedWhenChangingBegin()
    {
        time().tickMinute();
        final int row = time().getCurrentDayRowIndex();

        final ActivitiesTable activities = app().activitiesTable();

        activities.addRemainderActivity("act");

        final Builder expectedRow = ActivitiesTableExpectedRow.defaultRow().withRemainder(true).withComment("act");
        activities.table().assertContent(expectedRow.withDuration(Duration.ZERO).build());

        app().dayTable().typeBegin(row, "11:00");
        activities.table().assertContent(expectedRow.withDuration(Duration.ofMinutes(16)).build());
    }

    private void addActivity()
    {
        final ActivitiesTable activities = app().activitiesTable();

        final JavaFxTable<ActivityPropertyAdapter> table = activities.table();
        final boolean isFirstActivity = table.getRowCount() == 0;
        activities.addActivity();

        final Builder expectedRowContent = ActivitiesTableExpectedRow.defaultRow().withRemainder(isFirstActivity);

        assertAll(() -> table.assertRowCount(1),
                () -> table.assertRowContent(0, expectedRowContent.build()));
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
