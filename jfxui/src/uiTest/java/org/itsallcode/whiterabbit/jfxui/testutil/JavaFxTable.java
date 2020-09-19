package org.itsallcode.whiterabbit.jfxui.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.VirtualFlow;

public class JavaFxTable
{
    private static final Logger LOG = LogManager.getLogger(JavaFxTable.class);

    private final TableView<?> table;
    private final FxRobot robot;

    private JavaFxTable(TableView<?> table, FxRobot robot)
    {
        this.table = table;
        this.robot = robot;
    }

    public static JavaFxTable findDayTable(FxRobot robot)
    {
        return find(robot, "#day-table");
    }

    public static JavaFxTable findActivitiesTable(FxRobot robot)
    {
        return find(robot, "#activities-table");
    }

    private static JavaFxTable find(FxRobot robot, String query)
    {
        return new JavaFxTable(robot.lookup(query).queryTableView(), robot);
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
        LOG.debug("Table {} has {} rows", table, virtualFlow.getCellCount());
        return (TableRow<?>) virtualFlow.getCell(rowIndex);
    }

    public TableView<?> table()
    {
        return table;
    }
}
