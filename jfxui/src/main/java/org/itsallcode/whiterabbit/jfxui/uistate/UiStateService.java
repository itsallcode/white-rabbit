package org.itsallcode.whiterabbit.jfxui.uistate;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.itsallcode.whiterabbit.jfxui.uistate.widgets.WidgetRegistry;
import org.itsallcode.whiterabbit.jfxui.uistate.widgets.WidgetState;
import org.itsallcode.whiterabbit.logic.Config;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UiStateService
{
    private final WidgetRegistry state;

    private UiStateService(WidgetRegistry state)
    {
        this.state = state;
    }

    public static UiStateService loadState(Config config)
    {
        return new UiStateService(new WidgetRegistry());
    }

    public void persistState()
    {
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        final String json = jsonb.toJson(state);
        System.out.println(json);
    }

    public void register(String id, Stage stage)
    {
        final WidgetState<Stage> stageState = state.getStage(id);
        stage.addEventHandler(WindowEvent.WINDOW_HIDING, event -> stageState.store(stage));
        stageState.restore(stage);
    }

    public void register(Stage stage, TableView<?> node)
    {
        final WidgetState<TableView<?>> widgetState = state.getTableView(node.getId());
        stage.addEventHandler(WindowEvent.WINDOW_HIDING, event -> widgetState.store(node));
        widgetState.restore(node);
    }

    public void register(Stage stage, TreeTableView<?> node)
    {
        final WidgetState<TreeTableView<?>> widgetState = state.getTreeTableView(node.getId());
        stage.addEventHandler(WindowEvent.WINDOW_HIDING, event -> widgetState.store(node));
        widgetState.restore(node);
    }
}
