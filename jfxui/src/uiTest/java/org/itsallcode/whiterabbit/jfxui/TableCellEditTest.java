package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.itsallcode.whiterabbit.jfxui.testutil.DayTableExpectedRow;
import org.itsallcode.whiterabbit.jfxui.testutil.DayTableExpectedRow.Builder;
import org.itsallcode.whiterabbit.jfxui.testutil.model.JavaFxTable;
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
            final JavaFxTable dayTable = app().genericDayTable();
            final TableCell<?, ?> commentCell = dayTable.getTableCell(0, "comment");
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
        final LocalDate today = time().getCurrentDate();
        final LocalTime now = time().getCurrentTimeMinutes();
        final int rowIndex = time().getCurrentDayRowIndex();
        final String comment = "tst";

        final Builder expectedCellValues = DayTableExpectedRow.defaultValues(today, DayType.WORK);

        final JavaFxTable dayTable = app().genericDayTable();

        dayTable.assertRowContent(rowIndex, expectedCellValues.build());

        final TableCell<?, ?> commentCell = dayTable.getTableCell(rowIndex, "comment");

        robot.doubleClickOn(commentCell).write(comment);

        assertThat(commentCell.isEditing()).as("cell is editing").isTrue();

        commitAction.run();

        assertAll(
                () -> assertThat(commentCell.isEditing()).as("cell is editing").isFalse(),
                () -> dayTable.assertRowContent(rowIndex, expectedCellValues.withBegin(now).withEnd(now)
                        .withOvertimeToday(Duration.ofHours(-8))
                        .withTotalOvertime(Duration.ofHours(-8))
                        .withComment(comment).build()));
    }

    private void assertCommentCellNotPersistedAfterFocusLostAction(Runnable focusLossAction)
    {
        final LocalDate today = time().getCurrentDate();
        final int rowIndex = time().getCurrentDayRowIndex();

        final Builder expectedCellValues = DayTableExpectedRow.defaultValues(today, DayType.WORK);

        final JavaFxTable dayTable = app().genericDayTable();

        dayTable.assertRowContent(rowIndex, expectedCellValues.build());

        final TableCell<?, ?> commentCell = dayTable.getTableCell(rowIndex, "comment");

        robot.doubleClickOn(commentCell).write("tst");

        assertThat(commentCell.isEditing()).as("cell is editing").isTrue();

        focusLossAction.run();

        assertAll(
                () -> assertThat(commentCell.isEditing()).as("cell is editing").isFalse(),
                () -> dayTable.assertRowContent(rowIndex, expectedCellValues.build()));
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setInitialTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        doStart(stage);
        setRobot(robot);
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
