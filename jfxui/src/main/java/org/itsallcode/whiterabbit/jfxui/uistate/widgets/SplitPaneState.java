package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;

@JsonbVisibility(FieldAccessStrategy.class)
public class SplitPaneState implements WidgetState<SplitPane>
{
    String id;
    List<Double> dividerPositions;

    public SplitPaneState()
    {
        this(null);
    }

    SplitPaneState(String id)
    {
        this.id = id;
    }

    @Override
    public void restore(SplitPane widget)
    {
        if (dividerPositions == null || dividerPositions.size() != widget.getDividers().size())
        {
            return;
        }
        final double[] positions = dividerPositions.stream().filter(Objects::nonNull).mapToDouble(pos -> pos).toArray();
        widget.setDividerPositions(positions);
    }

    @Override
    public void watch(SplitPane widget)
    {
        final ObservableList<Divider> dividers = widget.getDividers();
        dividerPositions = new ArrayList<>(dividers.size());

        for (int i = 0; i < dividers.size(); i++)
        {
            dividerPositions.add(0.0);
            final int index = i;
            PropertyListener.register(
                    dividers.get(index).positionProperty(),
                    pos -> dividerPositions.set(index, pos.doubleValue()));
        }
    }
}
