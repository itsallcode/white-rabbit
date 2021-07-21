package org.itsallcode.whiterabbit.jfxui.uistate.model;

import java.util.List;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import jakarta.json.bind.annotation.JsonbVisibility;

@JsonbVisibility(FieldAccessStrategy.class)
@SuppressWarnings("java:S1104") // Encapsulation not necessary for model
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
