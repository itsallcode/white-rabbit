package org.itsallcode.whiterabbit.jfxui.uistate.model;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

@JsonbVisibility(FieldAccessStrategy.class)
@SuppressWarnings("java:S1104") // Encapsulation not necessary for model
public class ColumnStateModel
{
    public String id;
    public double width;

    public void setWidth(Number width)
    {
        this.width = width.doubleValue();
    }
}