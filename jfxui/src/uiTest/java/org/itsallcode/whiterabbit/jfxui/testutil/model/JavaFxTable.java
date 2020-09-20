package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.itsallcode.whiterabbit.jfxui.testutil.TableRowExpectedContent;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.VirtualFlow;

public class JavaFxTable
{
    private final TableView<?> table;

    private JavaFxTable(TableView<?> table)
    {
        this.table = table;
    }

    static JavaFxTable find(FxRobot robot, String query)
    {
        return new JavaFxTable(robot.lookup(query).queryTableView());
    }

    public void assertRowContent(final int rowIndex, final TableRowExpectedContent expectedRowContent)
    {
        Assertions.assertThat(table).containsRowAtIndex(rowIndex, expectedRowContent.expectedCellContent());
    }

    public TableCell<?, ?> getTableCell(final int rowIndex, final String columnId)
    {
        final TableRow<?> row = getTableRow(rowIndex);
        return row.getChildrenUnmodifiable().stream()
                .filter(cell -> cell.getId().equals(columnId))
                .map(TableCell.class::cast)
                .findFirst().orElseThrow();
    }

    public TableRow<?> getTableRow(final int rowIndex)
    {
        final VirtualFlow<?> virtualFlow = table.getChildrenUnmodifiable().stream()
                .filter(VirtualFlow.class::isInstance)
                .map(VirtualFlow.class::cast)
                .findFirst().orElseThrow();
        assertThat(virtualFlow.getCellCount()).isGreaterThan(rowIndex);
        return (TableRow<?>) virtualFlow.getCell(rowIndex);
    }

    public TableView<?> table()
    {
        return table;
    }
}
