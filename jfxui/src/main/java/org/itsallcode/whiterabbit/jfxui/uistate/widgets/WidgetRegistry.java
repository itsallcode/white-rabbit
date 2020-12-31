package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.annotation.JsonbVisibility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

@JsonbVisibility(FieldAccessStrategy.class)
public class WidgetRegistry
{
    private static final Logger LOG = LogManager.getLogger(WidgetRegistry.class);

    private final Map<String, StageState> stages = new HashMap<>();
    private final Map<String, TableState> tables = new HashMap<>();
    private final Map<String, TreeTableState> treeTables = new HashMap<>();

    public void registerStage(String id, Stage stage)
    {
        final WidgetState<Stage> state = stages.computeIfAbsent(id, StageState::new);

        state.restore(stage);
        state.watch(stage);
    }

    public void registerTableView(TableView<?> widget)
    {
        final WidgetState<TableView<?>> state = tables.computeIfAbsent(widget.getId(), TableState::new);

        state.restore(widget);
        state.watch(widget);
    }

    public void registerTreeTableView(TreeTableView<?> widget)
    {
        final WidgetState<TreeTableView<?>> state = treeTables.computeIfAbsent(widget.getId(), TreeTableState::new);

        state.restore(widget);
        state.watch(widget);
    }

    @Override
    public String toString()
    {
        return "WidgetRegistry [stages=" + stages + ", tables=" + tables + ", treeTables=" + treeTables + "]";
    }
}
