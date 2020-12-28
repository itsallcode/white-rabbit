package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.UiDebugTool;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.testfx.api.FxRobot;

import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;

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
        assertThat(getInterruption(row)).as("interruption").isEqualTo(expectedInterruption);
    }

    public void assertBeginAndEnd(int row, LocalTime begin, LocalTime end)
    {
        assertAll(
                () -> assertThat(getBegin(row)).as("begin").isEqualTo(begin),
                () -> assertThat(getEnd(row)).as("end").isEqualTo(end));
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
        int tries = 0;
        while (getDayType(row) != type && tries <= DayType.values().length)
        {
            final TableCell<?, ?> tableCell = getDayTypeCell(row);
            robot.clickOn(tableCell);
            TestUtil.sleepShort();
            robot.clickOn(tableCell);
            TestUtil.sleepShort();
            robot.type(KeyCode.DOWN);
            TestUtil.sleepShort();
            UiDebugTool.printNode(tableCell);
            robot.type(KeyCode.ENTER);

            tries++;
        }

        assertThat(getDayType(row)).isEqualTo(type);
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

    public JavaFxTable<DayRecordPropertyAdapter> table()
    {
        return table;
    }
}
