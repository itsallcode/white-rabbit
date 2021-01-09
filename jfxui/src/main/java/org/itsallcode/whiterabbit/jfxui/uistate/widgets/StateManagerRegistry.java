package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

public class StateManagerRegistry
{
    private final Map<Class<?>, ? extends WidgetStateManager<?, ?>> stateManagers;

    private StateManagerRegistry(Map<Class<?>, ? extends WidgetStateManager<?, ?>> stateManagers)
    {
        this.stateManagers = stateManagers;
    }

    public static StateManagerRegistry create()
    {
        final Map<Class<?>, WidgetStateManager<?, ?>> stateManagers = new HashMap<>();
        stateManagers.put(Stage.class, new StageStateManager());
        stateManagers.put(TableView.class, new TableStateManager());
        stateManagers.put(TreeTableView.class, new TreeTableStateManager());
        stateManagers.put(SplitPane.class, new SplitPaneStateManager());
        return new StateManagerRegistry(stateManagers);
    }

    public <T> WidgetStateManager<T, ?> getManager(Class<T> widgetType)
    {
        @SuppressWarnings("unchecked")
        final WidgetStateManager<T, ?> manager = (WidgetStateManager<T, ?>) stateManagers.get(widgetType);
        return manager;
    }
}
