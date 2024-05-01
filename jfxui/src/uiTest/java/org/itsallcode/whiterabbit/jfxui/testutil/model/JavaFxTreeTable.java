package org.itsallcode.whiterabbit.jfxui.testutil.model;

import java.util.List;

import org.testfx.api.FxRobot;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

public class JavaFxTreeTable<T>
{
    private final TreeTableView<T> table;
    private final Class<T> rowType;

    private JavaFxTreeTable(final TreeTableView<T> table, final Class<T> rowType)
    {
        this.table = table;
        this.rowType = rowType;
    }

    @SuppressWarnings("unchecked")
    static <T> JavaFxTreeTable<T> find(final FxRobot robot, final String query, final Class<T> rowType)
    {
        final TreeTableView<T> table = robot.lookup(query).queryAs(TreeTableView.class);
        return new JavaFxTreeTable<>(table, rowType);
    }

    List<T> getRootChildNodes()
    {
        return table.getRoot().getChildren().stream()
                .map(TreeItem::getValue)
                .map(rowType::cast)
                .toList();
    }

    List<T> getChildNodes(final int level1ChildIndex)
    {
        return table.getRoot().getChildren().get(level1ChildIndex)
                .getChildren().stream()
                .map(TreeItem::getValue)
                .map(rowType::cast)
                .toList();
    }
}
