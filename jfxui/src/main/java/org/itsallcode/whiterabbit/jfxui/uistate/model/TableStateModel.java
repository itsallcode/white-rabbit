package org.itsallcode.whiterabbit.jfxui.uistate.model;

import java.util.List;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

@JsonbVisibility(FieldAccessStrategy.class)
@SuppressWarnings("java:S1104") // Encapsulation not necessary for model
public class TableStateModel
{
    public String id;
    public List<ColumnStateModel> columns;

    public TableStateModel()
    {
        this(null);
    }

    public TableStateModel(String id)
    {
        this.id = id;
    }
}
