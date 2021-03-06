package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.testfx.api.FxRobot;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

public class JavaFxTreeTable<T>
{
    private final TreeTableView<T> table;

    private JavaFxTreeTable(FxRobot robot, TreeTableView<T> table)
    {
        this.table = table;
    }

    @SuppressWarnings("unchecked")
    static <T> JavaFxTreeTable<T> find(FxRobot robot, String query, Class<T> rowType)
    {
        return new JavaFxTreeTable<>(robot, robot.lookup(query).queryAs(TreeTableView.class));
    }

    List<T> getRootChildNodes()
    {
        return table.getRoot().getChildren().stream()
                .map(TreeItem::getValue)
                .collect(toList());
    }

    List<T> getChildNodes(int level1ChildIndex)
    {
        return table.getRoot().getChildren().get(level1ChildIndex)
                .getChildren().stream()
                .map(TreeItem::getValue)
                .collect(toList());
    }
}
