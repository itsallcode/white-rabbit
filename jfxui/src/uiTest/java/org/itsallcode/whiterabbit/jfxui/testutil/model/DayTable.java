package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

public class DayTable
{
    private static final Logger LOG = LogManager.getLogger(DayTable.class);

    private final JavaFxTable<DayRecordPropertyAdapter> table;
    private final FxRobot robot;

    DayTable(JavaFxTable<DayRecordPropertyAdapter> table, FxRobot robot)
    {
        this.table = table;
        this.robot = robot;
    }

    public DayRecord getSelectedRow()
    {
        return table.getSelectedTableRow().getItem().getRecord();
    }

    public void assertInterruption(int row, Duration expectedInterruption)
    {
        assertThat(getInterruption(row)).as("interruption duration").isEqualTo(expectedInterruption);
    }

    public void assertBeginAndEnd(int row, LocalTime begin, LocalTime end)
    {
        assertAll(
                () -> assertThat(getBegin(row)).as("begin time").isEqualTo(begin),
                () -> assertThat(getEnd(row)).as("end time").isEqualTo(end));
    }

    public void assertDate(int row, LocalDate expectedDate)
    {
        assertThat(getDate(row)).isEqualTo(expectedDate);
    }

    public LocalDate getDate(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "date");
        return (LocalDate) tableCell.getItem();
    }

    public LocalTime getBegin(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "begin");
        return (LocalTime) tableCell.getItem();
    }

    public String getBeginText(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "begin");
        return tableCell.getText();
    }

    public void typeBegin(int row, String value)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "begin");
        robot.clickOn(tableCell).clickOn(tableCell).clickOn(tableCell).type(KeyCode.BACK_SPACE).write(value)
                .type(KeyCode.TAB);
    }

    public LocalTime getEnd(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "end");
        return (LocalTime) tableCell.getItem();
    }

    public Duration getInterruption(int row)
    {
        final TableCell<?, ?> tableCell = getInterruptionCell(row);
        return (Duration) tableCell.getItem();
    }

    public String getInterruptionText(int row)
    {
        final TableCell<?, ?> tableCell = getInterruptionCell(row);
        return tableCell.getText();
    }

    public void typeInterruption(int row, String value)
    {
        final TableCell<?, ?> tableCell = getInterruptionCell(row);
        robot.doubleClickOn(tableCell).write(value).type(KeyCode.TAB);
    }

    private TableCell<?, ?> getInterruptionCell(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "interruption");
        return tableCell;
    }

    public void typeComment(int row, String value)
    {
        robot.doubleClickOn(getCommentCell(row)).write(value).type(KeyCode.TAB);
    }

    public TableCell<?, ?> getCommentCell(int row)
    {
        return table.getTableCell(row, "comment");
    }

    public void selectDayTypeDirect(int row, DayType type)
    {
        robot.doubleClickOn(getDayTypeCell(row));
        robot.clickOn(type.toString());
    }

    public void selectDayType(int row, DayType type)
    {
        final ChoiceBox<DayType> choiceBox = getChoiceBox(getDayTypeCell(row));
        JavaFxUtil.runOnFxApplicationThread(() -> {
            choiceBox.getSelectionModel().select(type);
        });
        assertThat(getDayType(row)).as("day type after selecting " + type).isEqualTo(type);
    }

    @SuppressWarnings("unchecked")
    private ChoiceBox<DayType> getChoiceBox(final TableCell<?, ?> tableCell)
    {
        if (!tableCell.isSelected())
        {
            robot.clickOn(tableCell);
        }

        final StackPane openButton = robot.from(tableCell).lookup(".open-button").queryAs(StackPane.class);
        robot.clickOn(openButton);
        final ChoiceBox<DayType> choiceBox = (ChoiceBox<DayType>) tableCell.graphicProperty().get();
        Assertions.assertThat(choiceBox).isNotNull().isVisible().isEnabled();
        return choiceBox;
    }

    public DayType getDayType(int row)
    {
        final TableCell<?, DayType> tableCell = getDayTypeCell(row);
        LOG.debug("Got day type {} for row {}", tableCell.getItem(), row);
        return tableCell.getItem();
    }

    @SuppressWarnings("unchecked")
    public TableCell<DayRecordPropertyAdapter, DayType> getDayTypeCell(int row)
    {
        return (TableCell<DayRecordPropertyAdapter, DayType>) table.getTableCell(row, "day-type");
    }

    public void assertRowsHighlightedAsWeekend(int... rows)
    {
        for (final int i : rows)
        {
            table.assertRowHasPseudoClass(i, "weekend");
        }
    }

    public void assertRowsNotHighlightedAsWeekend(int... rows)
    {
        for (final int i : rows)
        {
            table.assertRowDoesNotHavePseudoClass(i, "weekend");
        }
    }

    public JavaFxTable<DayRecordPropertyAdapter> table()
    {
        return table;
    }
}
