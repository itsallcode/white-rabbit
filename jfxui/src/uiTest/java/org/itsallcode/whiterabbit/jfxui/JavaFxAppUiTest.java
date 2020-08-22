package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.DayTableExpectedRow;
import org.itsallcode.whiterabbit.jfxui.testutil.DayTableExpectedRow.Builder;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.scene.control.Labeled;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class JavaFxAppUiTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void currentTimeAndOvertimeLabelsUpdated()
    {
        final Labeled timeLabel = robot.lookup("#current-time-label").queryLabeled();
        final Labeled overtimeLabel = robot.lookup("#overtime-label").queryLabeled();

        Assertions.assertThat(timeLabel).hasText("Current time: 03.12.07, 11:15:30");
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: 00:00, total: 00:00");

        tickSecond();
        Assertions.assertThat(timeLabel).hasText("Current time: 03.12.07, 11:15:31");
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: 00:00, total: 00:00");

        tickMinute();
        Assertions.assertThat(timeLabel).hasText("Current time: 03.12.07, 11:16:31");
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: -08:00, total: -08:00");

        tickMinute();
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: -07:59, total: -07:59");
    }

    @Test
    void dayTableUpdatedEveryMinute()
    {
        final Labeled timeLabel = robot.lookup("#current-time-label").queryLabeled();
        final Labeled overtimeLabel = robot.lookup("#overtime-label").queryLabeled();

        Assertions.assertThat(timeLabel).hasText("Current time: 03.12.07, 11:15:30");
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: 00:00, total: 00:00");

        tickSecond();
        Assertions.assertThat(timeLabel).hasText("Current time: 03.12.07, 11:15:31");
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: 00:00, total: 00:00");

        tickMinute();
        Assertions.assertThat(timeLabel).hasText("Current time: 03.12.07, 11:16:31");
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: -08:00, total: -08:00");

        tickMinute();
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: -07:59, total: -07:59");
    }

    @Test
    void jsonFileWrittenAfterMinuteTick()
    {
        final LocalDate today = getCurrentDate();

        tickMinute();
        final LocalTime begin = getCurrentTimeMinutes();

        tickMinute();
        final LocalTime end = getCurrentTimeMinutes();

        final JsonMonth month = loadMonth(today);

        assertThat(month.getDays()).hasSize(1);
        assertThat(month.getDays()).extracting(JsonDay::getBegin).containsExactly(begin);
        assertThat(month.getDays()).extracting(JsonDay::getEnd).containsExactly(end);
    }

    @Test
    void dayTableRowCount()
    {
        final TableView<DayRecordPropertyAdapter> dayTable = robot.lookup("#day-table").queryTableView();
        Assertions.assertThat(dayTable).hasExactlyNumRows(31);
    }

    @Test
    void enteredCommentPersistedAfterEnterKeyPressed()
    {
        assertCommentCellPersistedAfterCommitAction(() -> robot.type(KeyCode.ENTER));
    }

    @Test
    void enteredCommentPersistedAfterTabKeyPressed()
    {
        assertCommentCellPersistedAfterCommitAction(() -> robot.type(KeyCode.TAB));
    }

    @Test
    void enteredCommentPersistedAfterClickingAnotherRow()
    {
        assertCommentCellPersistedAfterCommitAction(() -> {
            final TableView<DayRecordPropertyAdapter> dayTable = robot.lookup("#day-table").queryTableView();
            final TableCell<?, ?> commentCell = getTableCell(dayTable, 0, "comment");
            robot.clickOn(commentCell);
        });
    }

    @Test
    void enteredCommentNotPersistedAfterTypingEscape()
    {
        assertCommentCellNotPersistedAfterFocusLostAction(() -> robot.type(KeyCode.ESCAPE));
    }

    private void assertCommentCellPersistedAfterCommitAction(Runnable commitAction)
    {
        final LocalDate today = getCurrentDate();
        final LocalTime now = getCurrentTimeMinutes();
        final int rowIndex = today.getDayOfMonth() - 1;

        final Builder expectedCellValues = DayTableExpectedRow.defaultValues(today, DayType.WORK);

        final TableView<DayRecordPropertyAdapter> dayTable = robot.lookup("#day-table").queryTableView();

        assertRowContent(dayTable, rowIndex, expectedCellValues.build());

        final TableCell<?, ?> commentCell = getTableCell(dayTable, rowIndex, "comment");

        robot.doubleClickOn(commentCell).type(KeyCode.A).type(KeyCode.B);

        assertThat(commentCell.isEditing()).isTrue();

        commitAction.run();

        assertThat(commentCell.isEditing()).isFalse();

        assertRowContent(dayTable, rowIndex, expectedCellValues.withBegin(now).withEnd(now)
                .withOvertimeToday(Duration.ofHours(-8))
                .withTotalOvertime(Duration.ofHours(-8))
                .withComment("ab").build());
    }

    private void assertCommentCellNotPersistedAfterFocusLostAction(Runnable focusLossAction)
    {
        final LocalDate today = getCurrentDate();
        final int rowIndex = today.getDayOfMonth() - 1;

        final Builder expectedCellValues = DayTableExpectedRow.defaultValues(today, DayType.WORK);

        final TableView<DayRecordPropertyAdapter> dayTable = robot.lookup("#day-table").queryTableView();

        assertRowContent(dayTable, rowIndex, expectedCellValues.build());

        final TableCell<?, ?> commentCell = getTableCell(dayTable, rowIndex, "comment");

        robot.doubleClickOn(commentCell).type(KeyCode.A).type(KeyCode.B);

        assertThat(commentCell.isEditing()).isTrue();

        focusLossAction.run();

        assertThat(commentCell.isEditing()).isFalse();

        assertRowContent(dayTable, rowIndex, expectedCellValues.build());
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setCurrentTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        doStart(stage);
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
