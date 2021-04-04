package org.itsallcode.whiterabbit.jfxui.uistate.model;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

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
