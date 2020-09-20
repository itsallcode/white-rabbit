package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.testfx.api.FxRobot;

import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;

public class DayTable
{
    private static final Logger LOG = LogManager.getLogger(DayTable.class);

    private final JavaFxTable table;
    private final FxRobot robot;

    public DayTable(JavaFxTable table, FxRobot robot)
    {
        this.table = table;
        this.robot = robot;
    }

    public static DayTable find(FxRobot robot)
    {
        return new DayTable(JavaFxTable.findDayTable(robot), robot);
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

    public LocalTime getEnd(int row)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "end");
        return (LocalTime) tableCell.getItem();
    }

    public void selectDayType(int row, DayType type)
    {
        final TableCell<?, ?> tableCell = table.getTableCell(row, "day-type");
        int tries = 0;
        robot.clickOn(tableCell);
        TestUtil.sleepShort();
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
