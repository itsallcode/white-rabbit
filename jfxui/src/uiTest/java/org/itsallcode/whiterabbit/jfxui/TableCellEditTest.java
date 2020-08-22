package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.DayTableExpectedRow;
import org.itsallcode.whiterabbit.jfxui.testutil.DayTableExpectedRow.Builder;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class TableCellEditTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void commentPersistedAfterEnterKeyPressed()
    {
        assertCommentCellPersistedAfterCommitAction(() -> robot.type(KeyCode.ENTER));
    }

    @Test
    void commentPersistedAfterTabKeyPressed()
    {
        assertCommentCellPersistedAfterCommitAction(() -> robot.type(KeyCode.TAB));
    }

    @DisabledIfSystemProperty(named = "testfx.headless", matches = "true")
    @Test
    void commentPersistedAfterOtherWindowFocused()
    {
        assertCommentCellPersistedAfterCommitAction(() -> {
            final AtomicReference<Stage> otherWindow = new AtomicReference<>();
            robot.interact(() -> otherWindow.set(openOtherWindow()));
            robot.clickOn(otherWindow.get());
        });
    }

    private Stage openOtherWindow()
    {
        final Stage stage = new Stage();
        stage.setTitle("Other Window");
        stage.setScene(new Scene(new StackPane(), 230, 100));
        stage.show();
        return stage;
    }

    @Test
    void commentNotPersistedAfterClickingAnotherRow()
    {
        assertCommentCellNotPersistedAfterFocusLostAction(() -> {
            final TableView<DayRecordPropertyAdapter> dayTable = robot.lookup("#day-table").queryTableView();
            final TableCell<?, ?> commentCell = getTableCell(dayTable, 0, "comment");
            robot.clickOn(commentCell);
        });
    }

    @Test
    void commentNotPersistedAfterTypingEscape()
    {
        assertCommentCellNotPersistedAfterFocusLostAction(() -> robot.type(KeyCode.ESCAPE));
    }

    private void assertCommentCellPersistedAfterCommitAction(Runnable commitAction)
    {
        final LocalDate today = getCurrentDate();
        final LocalTime now = getCurrentTimeMinutes();
        final int rowIndex = getCurrentDayRowIndex();
        final String comment = "tst";

        final Builder expectedCellValues = DayTableExpectedRow.defaultValues(today, DayType.WORK);

        final TableView<DayRecordPropertyAdapter> dayTable = robot.lookup("#day-table").queryTableView();

        assertRowContent(dayTable, rowIndex, expectedCellValues.build());

        final TableCell<?, ?> commentCell = getTableCell(dayTable, rowIndex, "comment");

        robot.doubleClickOn(commentCell).write(comment);

        assertThat(commentCell.isEditing()).as("cell is editing").isTrue();

        commitAction.run();

        assertThat(commentCell.isEditing()).as("cell is editing").isFalse();

        assertRowContent(dayTable, rowIndex, expectedCellValues.withBegin(now).withEnd(now)
                .withOvertimeToday(Duration.ofHours(-8))
                .withTotalOvertime(Duration.ofHours(-8))
                .withComment(comment).build());
    }

    private void assertCommentCellNotPersistedAfterFocusLostAction(Runnable focusLossAction)
    {
        final LocalDate today = getCurrentDate();
        final int rowIndex = getCurrentDayRowIndex();

        final Builder expectedCellValues = DayTableExpectedRow.defaultValues(today, DayType.WORK);

        final TableView<DayRecordPropertyAdapter> dayTable = robot.lookup("#day-table").queryTableView();

        assertRowContent(dayTable, rowIndex, expectedCellValues.build());

        final TableCell<?, ?> commentCell = getTableCell(dayTable, rowIndex, "comment");

        robot.doubleClickOn(commentCell).write("tst");

        assertThat(commentCell.isEditing()).as("cell is editing").isTrue();

        focusLossAction.run();

        assertThat(commentCell.isEditing()).as("cell is editing").isFalse();

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
