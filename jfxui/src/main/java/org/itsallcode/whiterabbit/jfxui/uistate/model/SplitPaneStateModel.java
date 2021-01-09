package org.itsallcode.whiterabbit.jfxui.uistate.model;

import java.util.List;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

@JsonbVisibility(FieldAccessStrategy.class)
public class SplitPaneStateModel
{
    public String id;
    public List<Double> dividerPositions;

    public SplitPaneStateModel()
    {
        this(null);
    }

    public SplitPaneStateModel(String id)
    {
        this.id = id;
    }
}
