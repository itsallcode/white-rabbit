package org.itsallcode.whiterabbit.jfxui.uistate.model;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import jakarta.json.bind.annotation.JsonbVisibility;

@JsonbVisibility(FieldAccessStrategy.class)
@SuppressWarnings("java:S1104") // Encapsulation not necessary for model
public class ColumnStateModel
{
    public String id;
    public Double width;

    public void setWidth(Number width)
    {
        this.width = StateModelUtil.assertValidDouble(width);
    }
}