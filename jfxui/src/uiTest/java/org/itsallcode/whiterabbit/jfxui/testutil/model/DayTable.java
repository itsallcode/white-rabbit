package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.*;

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
            assertThat(getInterruption()).as("interruption duration in row " + row).isEqualTo(expectedInterruption);
        }

        public void assertBeginAndEnd(final LocalTime begin, final LocalTime end)
        {
            assertAll(
                    () -> assertThat(getBegin()).as("begin time in row " + row).isEqualTo(begin),
                    () -> assertThat(getEnd()).as("end time in row " + row).isEqualTo(end));
        }

        public void assertDate(final LocalDate expectedDate)
        {
            assertThat(getDate()).as("date in row " + row).isEqualTo(expectedDate);
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
            LOG.debug("Type begin value '{}' into row {}", value, row);
            final TableCell<?, ?> tableCell = table.row(row).cell("begin");
            robot.clickOn(tableCell).doubleClickOn(tableCell).type(KeyCode.BACK_SPACE).write(value)
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
            return table.row(row).cell("comment");
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
            return (TableCell<DayRecordPropertyAdapter, DayType>) table.row(row).cell("day-type");
        }
    }
}
