package org.itsallcode.whiterabbit.jfxui.uistate.model;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import jakarta.json.bind.annotation.JsonbVisibility;

@JsonbVisibility(FieldAccessStrategy.class)
@SuppressWarnings("java:S1104") // Encapsulation not necessary for model
public class StageStateModel
{
    public String id;
    public Double x;
    public Double y;
    public Double width;
    public Double height;

    public StageStateModel()
    {
        this(null);
    }

    public StageStateModel(String id)
    {
        this.id = id;
    }

    public void setX(Number x)
    {
        this.x = StateModelUtil.assertValidDouble(x);
    }

    public void setY(Number y)
    {
        this.y = StateModelUtil.assertValidDouble(y);
    }

    public void setWidth(Number width)
    {
        this.width = StateModelUtil.assertValidDouble(width);
    }

    public void setHeight(Number height)
    {
        this.height = StateModelUtil.assertValidDouble(height);
    }
}
