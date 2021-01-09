package org.itsallcode.whiterabbit.jfxui.uistate.model;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

@JsonbVisibility(FieldAccessStrategy.class)
public class ColumnStateModel
{
    public String id;
    public double width;

    public void setWidth(Number width)
    {
        this.width = width.doubleValue();
    }
}