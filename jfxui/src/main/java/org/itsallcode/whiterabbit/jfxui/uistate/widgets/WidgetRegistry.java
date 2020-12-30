package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

public class WidgetRegistry
{
    private final Map<String, WidgetState<Stage>> stages = new HashMap<>();
    private final Map<String, WidgetState<TableView<?>>> tables = new HashMap<>();
    private final Map<String, WidgetState<TreeTableView<?>>> treeTables = new HashMap<>();

    public WidgetState<Stage> getStage(String id)
    {
        return stages.computeIfAbsent(id, i -> new StageState());
    }

    public WidgetState<TableView<?>> getTableView(String id)
    {
        return tables.computeIfAbsent(id, i -> new TableState());
    }

    public WidgetState<TreeTableView<?>> getTreeTableView(String id)
    {
        return treeTables.computeIfAbsent(id, i -> new TreeTableState());
    }
}
