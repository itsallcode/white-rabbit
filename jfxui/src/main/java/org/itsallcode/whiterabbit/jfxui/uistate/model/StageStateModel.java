package org.itsallcode.whiterabbit.jfxui.uistate.model;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

@JsonbVisibility(FieldAccessStrategy.class)
public class StageStateModel
{
    public String id;
    public double x;
    public double y;
    public double width;
    public double height;

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
        this.x = x.doubleValue();
    }

    public void setY(Number y)
    {
        this.y = y.doubleValue();
    }

    public void setWidth(Number width)
    {
        this.width = width.doubleValue();
    }

    public void setHeight(Number height)
    {
        this.height = height.doubleValue();
    }
}
