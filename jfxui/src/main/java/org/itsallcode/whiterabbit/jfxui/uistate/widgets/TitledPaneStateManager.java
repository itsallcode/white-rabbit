package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import org.itsallcode.whiterabbit.jfxui.property.DelayedPropertyListener;
import org.itsallcode.whiterabbit.jfxui.uistate.model.TitledPaneStateModel;

import javafx.scene.control.TitledPane;

class TitledPaneStateManager implements WidgetStateManager<TitledPane, TitledPaneStateModel>
{
    private final DelayedPropertyListener propertyListener;

    TitledPaneStateManager(DelayedPropertyListener propertyListener)
    {
        this.propertyListener = propertyListener;
    }

    @Override
    public void restore(TitledPane widget, TitledPaneStateModel model)
    {
        if (model.expanded == null)
        {
            return;
        }
        widget.setExpanded(model.expanded);
    }

    @Override
    public void watch(TitledPane widget, TitledPaneStateModel model)
    {
        propertyListener.register(widget.expandedProperty(), expanded -> model.expanded = expanded);
    }

    @Override
    public TitledPaneStateModel createEmptyModel(String id)
    {
        return new TitledPaneStateModel(id);
    }
}
