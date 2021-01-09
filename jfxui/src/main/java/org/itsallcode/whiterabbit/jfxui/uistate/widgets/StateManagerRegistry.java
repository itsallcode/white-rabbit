package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.HashMap;
import java.util.Map;

import org.itsallcode.whiterabbit.jfxui.property.DelayedPropertyListener;

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

    public static StateManagerRegistry create(DelayedPropertyListener propertyListener)
    {
        final Map<Class<?>, WidgetStateManager<?, ?>> stateManagers = new HashMap<>();
        stateManagers.put(Stage.class, new StageStateManager(propertyListener));
        stateManagers.put(TableView.class, new TableStateManager(propertyListener));
        stateManagers.put(TreeTableView.class, new TreeTableStateManager(propertyListener));
        stateManagers.put(SplitPane.class, new SplitPaneStateManager(propertyListener));
        return new StateManagerRegistry(stateManagers);
    }

    public <T> WidgetStateManager<T, ?> getManager(Class<T> widgetType)
    {
        @SuppressWarnings("unchecked")
        final WidgetStateManager<T, ?> manager = (WidgetStateManager<T, ?>) stateManagers.get(widgetType);
        return manager;
    }
}
