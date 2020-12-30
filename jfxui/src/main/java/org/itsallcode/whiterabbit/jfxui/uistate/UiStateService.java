package org.itsallcode.whiterabbit.jfxui.uistate;

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

    }

    public void register(String id, Stage stage)
    {
        final WidgetState<Stage> stageState = state.getStage(id);
        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> stageState.store(stage));
        stageState.restore(stage);
    }

    public void register(Stage stage, TableView<?> node)
    {
        final WidgetState<TableView<?>> widgetState = state.getTableView(node.getId());
        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> widgetState.store(node));
        widgetState.restore(node);
    }

    public void register(Stage stage, TreeTableView<?> node)
    {
        final WidgetState<TreeTableView<?>> widgetState = state.getTreeTableView(node.getId());
        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> widgetState.store(node));
        widgetState.restore(node);
    }
}
