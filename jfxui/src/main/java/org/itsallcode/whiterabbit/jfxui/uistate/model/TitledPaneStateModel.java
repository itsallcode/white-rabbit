package org.itsallcode.whiterabbit.jfxui.uistate.model;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import jakarta.json.bind.annotation.JsonbVisibility;

@JsonbVisibility(FieldAccessStrategy.class)
@SuppressWarnings("java:S1104") // Encapsulation not necessary for model
public class TitledPaneStateModel
{
    public Boolean expanded;
    public String id;

    public TitledPaneStateModel()
    {
        this(null);
    }

    public TitledPaneStateModel(String id)
    {
        this.id = id;
    }
}
