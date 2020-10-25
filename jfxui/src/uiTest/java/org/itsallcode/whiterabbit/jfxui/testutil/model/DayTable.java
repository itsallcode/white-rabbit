package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
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
        robot.doubleClickOn(tableCell).write(value).type(KeyCode.TAB);
    }

    public LocalTime getEnd(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "end");
        return (LocalTime) tableCell.getItem();
    }

    public Duration getInterruption(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "interruption");
        return (Duration) tableCell.getItem();
    }

    public String getInterruptionText(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "interruption");
        return tableCell.getText();
    }

    public void typeInterruption(int row, String value)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "interruption");
        robot.doubleClickOn(tableCell).write(value).type(KeyCode.TAB);
    }

    public void selectDayType(int row, DayType type)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "day-type");
        int tries = 0;
        while (getDayType(row) != type && tries <= DayType.values().length)
        {
            robot.clickOn(tableCell);
            TestUtil.sleepShort();
            robot.clickOn(tableCell);
            TestUtil.sleepShort();
            robot.type(KeyCode.DOWN);
            TestUtil.sleepShort();
            robot.type(KeyCode.ENTER);

            tries++;
        }

        assertThat(getDayType(row)).isEqualTo(type);
    }

    public DayType getDayType(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "day-type");
        LOG.debug("Got day type {} for row {}", tableCell.getItem(), row);
        return (DayType) tableCell.getItem();
    }
}
