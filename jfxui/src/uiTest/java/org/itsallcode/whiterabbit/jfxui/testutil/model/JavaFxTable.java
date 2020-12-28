package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.TableRowExpectedContent;
import org.junit.jupiter.api.function.Executable;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.VirtualFlow;

public class JavaFxTable<T>
{
    private static final Logger LOG = LogManager.getLogger(JavaFxTable.class);

    private final FxRobot robot;
    private final TableView<T> table;

    private JavaFxTable(FxRobot robot, TableView<T> table)
    {
        this.robot = robot;
        this.table = table;
    }

    static <T> JavaFxTable<T> find(FxRobot robot, String query, Class<T> rowType)
    {
        return new JavaFxTable<>(robot, robot.lookup(query).queryTableView());
    }

    public void assertContent(TableRowExpectedContent... expectedRows)
    {
        final List<Executable> expectations = new ArrayList<>();
        expectations.add(() -> assertRowCount(expectedRows.length));
        for (int i = 0; i < expectedRows.length; i++)
        {
            final int row = i;
            expectations.add(() -> assertRowContent(row, expectedRows[row]));
        }
        assertAll(expectations);
    }

    public void assertRowContent(final int rowIndex, final TableRowExpectedContent expectedRowContent)
    {
        Assertions.assertThat(table).containsRowAtIndex(rowIndex, expectedRowContent.expectedCellContent());
    }

    public TableCell<?, ?> getTableCell(final int rowIndex, final String columnId)
    {
        LOG.debug("Getting row {} / column {}", rowIndex, columnId);
        final IndexedCell<T> row = getTableRow(rowIndex);
        return row.getChildrenUnmodifiable().stream()
                .filter(cell -> cell.getId().equals(columnId))
                .map(TableCell.class::cast)
                .findFirst().orElseThrow();
    }

    public IndexedCell<T> getTableRow(final int rowIndex)
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

    public JavaFxTable<T> clickRow(int rowIndex)
    {
        robot.clickOn(getTableRow(rowIndex));
        return this;
    }

    public JavaFxTable<T> assertRowCount(int expectedRowCount)
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
        return getTableRow(selectedRowIndex);
    }
}
