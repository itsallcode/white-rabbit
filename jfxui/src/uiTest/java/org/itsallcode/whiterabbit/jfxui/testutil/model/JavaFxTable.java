package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.TableRowExpectedContent;
import org.junit.jupiter.api.function.Executable;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.css.PseudoClass;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.VirtualFlow;

public class JavaFxTable<T>
{
    private static final Logger LOG = LogManager.getLogger(JavaFxTable.class);

    private final FxRobot robot;
    private final TableView<T> table;

    private JavaFxTable(final FxRobot robot, final TableView<T> table)
    {
        this.robot = robot;
        this.table = table;
    }

    static <T> JavaFxTable<T> find(final FxRobot robot, final String query, final Class<T> rowType)
    {
        return new JavaFxTable<>(robot, robot.lookup(query).queryTableView());
    }

    public JavaFxTableRow row(final int rowIndex)
    {
        return new JavaFxTableRow(rowIndex);
    }

    public void assertContent(final TableRowExpectedContent... expectedRows)
    {
        final List<Executable> expectations = new ArrayList<>();
        expectations.add(() -> assertRowCount(expectedRows.length));
        for (int i = 0; i < expectedRows.length; i++)
        {
            final int row = i;
            expectations.add(() -> row(row).assertContent(expectedRows[row]));
        }
        assertAll(expectations);
    }

    public JavaFxTable<T> doubleClick()
    {
        robot.doubleClickOn(table);
        return this;
    }

    public JavaFxTable<T> assertRowCount(final int expectedRowCount)
    {
        Assertions.assertThat(table).hasExactlyNumRows(expectedRowCount);
        return this;
    }

    public int getRowCount()
    {
        return table.getItems().size();
    }

    public int getSelectedRowIndex()
    {
        return table.getSelectionModel().selectedIndexProperty().get();
    }

    public IndexedCell<T> getSelectedTableRow()
    {
        final int selectedRowIndex = getSelectedRowIndex();
        if (selectedRowIndex < 0)
        {
            throw new AssertionError("No row is selected");
        }
        return row(selectedRowIndex).tableRow();
    }

    public void assertRowSelected(final int expectedRow)
    {
        assertThat(getSelectedRowIndex()).as("selected row").isEqualTo(expectedRow);
    }

    public void assertNoRowSelected()
    {
        assertThat(getSelectedRowIndex()).as("selected row index").isNegative();
    }

    public class JavaFxTableRow
    {
        private final int rowIndex;

        private JavaFxTableRow(final int rowIndex)
        {
            this.rowIndex = rowIndex;
        }

        public void assertContent(final TableRowExpectedContent expectedRowContent)
        {
            Assertions.assertThat(table).containsRowAtIndex(rowIndex, expectedRowContent.expectedCellContent());
        }

        public TableCell<?, ?> cell(final String columnId)
        {
            LOG.debug("Getting row {} / column {}", rowIndex, columnId);
            final IndexedCell<T> row = tableRow();
            return row.getChildrenUnmodifiable().stream()
                    .filter(cell -> cell.getId().equals(columnId))
                    .map(TableCell.class::cast)
                    .findFirst().orElseThrow();
        }

        public IndexedCell<T> tableRow()
        {
            @SuppressWarnings("unchecked")
            final VirtualFlow<IndexedCell<T>> virtualFlow = table.getChildrenUnmodifiable().stream()
                    .filter(VirtualFlow.class::isInstance)
                    .map(VirtualFlow.class::cast)
                    .findFirst().orElseThrow();
            assertThat(virtualFlow.getCellCount()).as("row count of " + virtualFlow).isGreaterThan(rowIndex);
            final IndexedCell<T> row = JavaFxUtil.runOnFxApplicationThread(() -> virtualFlow.getCell(rowIndex));
            LOG.debug("Got row #{} of {}: {}", rowIndex, virtualFlow, row);
            return row;
        }

        public void assertHasPseudoClass(final String expectedClass)
        {
            assertThat(getPseudoClass(expectedClass))
                    .as("Pseudo classes of row " + rowIndex + " with name " + expectedClass).hasSize(1);
        }

        public void assertDoesNotHavePseudoClass(final String expectedClass)
        {
            assertThat(getPseudoClass(expectedClass))
                    .as("Pseudo classes of row " + rowIndex + " with name " + expectedClass).isEmpty();
        }

        private Stream<PseudoClass> getPseudoClass(final String pseudoClassName)
        {
            return tableRow().getPseudoClassStates().stream()
                    .filter(pseudoClass -> pseudoClass.getPseudoClassName().equals(pseudoClassName));
        }

        public JavaFxTable<T>.JavaFxTableRow clickRow(final int rowIndex)
        {
            robot.clickOn(tableRow());
            return this;
        }

        public JavaFxTable<T>.JavaFxTableRow doubleClick()
        {
            robot.doubleClickOn(tableRow());
            return this;
        }
    }
}
