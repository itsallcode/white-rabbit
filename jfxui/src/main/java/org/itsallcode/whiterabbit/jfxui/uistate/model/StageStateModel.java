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

        this.x = assertValid(x);
    }

    private Double assertValid(Number n)
    {
        final double d = n.doubleValue();
        if (Double.isInfinite(d))
        {
            return null;
        }
        if (Double.isNaN(d))
        {
            return null;
        }
        return d;
    }

    public void setY(Number y)
    {
        this.y = assertValid(y);
    }

    public void setWidth(Number width)
    {
        this.width = assertValid(width);
    }

    public void setHeight(Number height)
    {
        this.height = assertValid(height);
    }
}
