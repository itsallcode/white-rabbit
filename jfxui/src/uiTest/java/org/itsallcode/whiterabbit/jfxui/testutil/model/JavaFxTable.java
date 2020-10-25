package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.itsallcode.whiterabbit.jfxui.testutil.TableRowExpectedContent;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.VirtualFlow;

public class JavaFxTable<T>
{
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

    public void assertRowContent(final int rowIndex, final TableRowExpectedContent expectedRowContent)
    {
        Assertions.assertThat(table).containsRowAtIndex(rowIndex, expectedRowContent.expectedCellContent());
    }

    public TableCell<?, ?> getTableCell(final int rowIndex, final String columnId)
    {
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
        assertThat(virtualFlow.getCellCount()).isGreaterThan(rowIndex);
        return virtualFlow.getCell(rowIndex);
    }

    public JavaFxTable<T> clickRow(int rowIndex)
    {
        robot.clickOn(getTableRow(rowIndex));
        return this;
    }

    public void assertRowCount(int expectedRowCount)
    {
        assertThat(table.getItems()).hasSize(expectedRowCount);
    }

    public TableView<?> table()
    {
        return table;
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
