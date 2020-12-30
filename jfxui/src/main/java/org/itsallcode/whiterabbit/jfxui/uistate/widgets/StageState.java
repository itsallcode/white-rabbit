package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import javafx.stage.Stage;

@JsonbVisibility(FieldAccessStrategy.class)
class StageState implements WidgetState<Stage>
{
    private double x;
    private double y;
    private double width;
    private double height;

    @Override
    public void store(Stage stage)
    {
        System.out.println("Store " + stage);
        this.x = stage.getX();
        this.y = stage.getY();
        this.width = stage.getWidth();
        this.height = stage.getHeight();
    }

    @Override
    public void restore(Stage stage)
    {
        if (width == 0 || height == 0)
        {
            System.out.println("don't restore state ");
            return;
        }
        System.out.println("Re-Store " + stage);
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);
    }
}
