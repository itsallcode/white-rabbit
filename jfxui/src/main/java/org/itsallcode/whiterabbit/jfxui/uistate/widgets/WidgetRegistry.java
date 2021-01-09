package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.json.bind.annotation.JsonbVisibility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

@JsonbVisibility(FieldAccessStrategy.class)
public class WidgetRegistry
{
    private static final Logger LOG = LogManager.getLogger(WidgetRegistry.class);

    Map<String, StageState> stages = new HashMap<>();
    Map<String, TableState> tables = new HashMap<>();
    Map<String, TreeTableState> treeTables = new HashMap<>();
    Map<String, SplitPaneState> splitPanes = new HashMap<>();

    public void registerStage(String id, Stage stage)
    {
        final WidgetState<Stage> state = stages.computeIfAbsent(Objects.requireNonNull(id), StageState::new);

        state.restore(stage);
        state.watch(stage);
    }

    public void registerTableView(TableView<?> widget)
    {
        final WidgetState<TableView<?>> state = tables.computeIfAbsent(Objects.requireNonNull(widget.getId()),
                TableState::new);

        state.restore(widget);
        state.watch(widget);
    }

    public void registerTreeTableView(TreeTableView<?> widget)
    {
        final WidgetState<TreeTableView<?>> state = treeTables.computeIfAbsent(Objects.requireNonNull(widget.getId()),
                TreeTableState::new);

        state.restore(widget);
        state.watch(widget);
    }

    public void registerSplitPane(SplitPane pane)
    {
        final WidgetState<SplitPane> state = splitPanes.computeIfAbsent(Objects.requireNonNull(pane.getId()),
                SplitPaneState::new);

        state.restore(pane);
        state.watch(pane);
    }

    @Override
    public String toString()
    {
        return "WidgetRegistry [stages=" + stages + ", tables=" + tables + ", treeTables=" + treeTables
                + ", splitPanes=" + splitPanes + "]";
    }
}
