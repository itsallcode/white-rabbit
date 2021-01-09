package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.ArrayList;
import java.util.Objects;

import org.itsallcode.whiterabbit.jfxui.property.DelayedPropertyListener;
import org.itsallcode.whiterabbit.jfxui.uistate.model.SplitPaneStateModel;

import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;

class SplitPaneStateManager implements WidgetStateManager<SplitPane, SplitPaneStateModel>
{
    private final DelayedPropertyListener propertyListener;

    SplitPaneStateManager(DelayedPropertyListener propertyListener)
    {
        this.propertyListener = propertyListener;
    }

    @Override
    public void restore(SplitPane widget, SplitPaneStateModel model)
    {
        if (model.dividerPositions == null || model.dividerPositions.size() != widget.getDividers().size())
        {
            return;
        }
        final double[] positions = model.dividerPositions.stream().filter(Objects::nonNull).mapToDouble(pos -> pos)
                .toArray();
        widget.setDividerPositions(positions);
    }

    @Override
    public void watch(SplitPane widget, SplitPaneStateModel model)
    {
        final ObservableList<Divider> dividers = widget.getDividers();
        model.dividerPositions = new ArrayList<>(dividers.size());

        for (int i = 0; i < dividers.size(); i++)
        {
            model.dividerPositions.add(0.0);
            final int index = i;
            propertyListener.register(
                    dividers.get(index).positionProperty(),
                    pos -> model.dividerPositions.set(index, pos.doubleValue()));
        }
    }

    @Override
    public SplitPaneStateModel createEmptyModel(String id)
    {
        return new SplitPaneStateModel(id);
    }
}
