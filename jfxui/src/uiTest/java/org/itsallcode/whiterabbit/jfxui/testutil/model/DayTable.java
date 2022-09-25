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

    DayTable(final JavaFxTable<DayRecordPropertyAdapter> table, final FxRobot robot)
    {
        this.table = table;
        this.robot = robot;
    }

    public DayTableRow row(final int rowIndex)
    {
        return new DayTableRow(rowIndex);
    }

    public DayRecord getSelectedRow()
    {
        return table.getSelectedTableRow().getItem().getRecord();
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

    public void assertRowsHighlightedAsWeekend(final int... rows)
    {
        for (final int i : rows)
        {
            table.row(i).assertHasPseudoClass("weekend");
        }
    }

    public void assertRowsNotHighlightedAsWeekend(final int... rows)
    {
        for (final int i : rows)
        {
            table.row(i).assertDoesNotHavePseudoClass("weekend");
        }
    }

    public JavaFxTable<DayRecordPropertyAdapter> table()
    {
        return table;
    }

    public class DayTableRow
    {
        private final int row;

        private DayTableRow(final int rowIndex)
        {
            this.row = rowIndex;
        }

        public void assertInterruption(final Duration expectedInterruption)
        {
            assertThat(getInterruption()).as("interruption duration").isEqualTo(expectedInterruption);
        }

        public void assertBeginAndEnd(final LocalTime begin, final LocalTime end)
        {
            assertAll(
                    () -> assertThat(getBegin()).as("begin time").isEqualTo(begin),
                    () -> assertThat(getEnd()).as("end time").isEqualTo(end));
        }

        public void assertDate(final LocalDate expectedDate)
        {
            assertThat(getDate()).isEqualTo(expectedDate);
        }

        public LocalDate getDate()
        {
            final TableCell<?, ?> tableCell = table.row(row).cell("date");
            return (LocalDate) tableCell.getItem();
        }

        public LocalTime getBegin()
        {
            final TableCell<?, ?> tableCell = table.row(row).cell("begin");
            return (LocalTime) tableCell.getItem();
        }

        public String getBeginText()
        {
            final TableCell<?, ?> tableCell = table.row(row).cell("begin");
            return tableCell.getText();
        }

        public void typeBegin(final String value)
        {
            final TableCell<?, ?> tableCell = table.row(row).cell("begin");
            robot.clickOn(tableCell).clickOn(tableCell).clickOn(tableCell).type(KeyCode.BACK_SPACE).write(value)
                    .type(KeyCode.TAB);
        }

        public LocalTime getEnd()
        {
            final TableCell<?, ?> tableCell = table.row(row).cell("end");
            return (LocalTime) tableCell.getItem();
        }

        public Duration getInterruption()
        {
            final TableCell<?, ?> tableCell = getInterruptionCell();
            return (Duration) tableCell.getItem();
        }

        public String getInterruptionText()
        {
            final TableCell<?, ?> tableCell = getInterruptionCell();
            return tableCell.getText();
        }

        public void typeInterruption(final String value)
        {
            final TableCell<?, ?> tableCell = getInterruptionCell();
            robot.doubleClickOn(tableCell).write(value).type(KeyCode.TAB);
        }

        private TableCell<?, ?> getInterruptionCell()
        {
            final TableCell<?, ?> tableCell = table.row(row).cell("interruption");
            return tableCell;
        }

        public void typeComment(final String value)
        {
            robot.doubleClickOn(getCommentCell()).write(value).type(KeyCode.TAB);
        }

        public TableCell<?, ?> getCommentCell()
        {
            return table.getTableCell("comment");
        }

        public void selectDayTypeDirect(final DayType type)
        {
            robot.doubleClickOn(getDayTypeCell());
            robot.clickOn(type.toString());
        }

        public void selectDayType(final DayType type)
        {
            final ChoiceBox<DayType> choiceBox = getChoiceBox(getDayTypeCell());
            JavaFxUtil.runOnFxApplicationThread(() -> {
                choiceBox.getSelectionModel().select(type);
            });
            assertThat(getDayType()).as("day type after selecting " + type).isEqualTo(type);
        }

        public DayType getDayType()
        {
            final TableCell<?, DayType> tableCell = getDayTypeCell();
            LOG.debug("Got day type {} for row {}", tableCell.getItem(), row);
            return tableCell.getItem();
        }

        @SuppressWarnings("unchecked")
        public TableCell<DayRecordPropertyAdapter, DayType> getDayTypeCell()
        {
            return (TableCell<DayRecordPropertyAdapter, DayType>) table.getTableCell(row, "day-type");
        }
    }
}
