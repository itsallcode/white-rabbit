package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import javax.json.bind.annotation.JsonbVisibility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import javafx.stage.Stage;

@JsonbVisibility(FieldAccessStrategy.class)
public class StageState implements WidgetState<Stage>
{
    private static final Logger LOG = LogManager.getLogger(StageState.class);

    private double x;
    private double y;
    private double width;
    private double height;
    private final String stageId;

    public StageState()
    {
        this(null);
    }

    StageState(String id)
    {
        this.stageId = id;
    }

    @Override
    public void watch(Stage stage)
    {
        PropertyListener.register(stage.xProperty(), this::setX);
        PropertyListener.register(stage.yProperty(), this::setY);
        PropertyListener.register(stage.widthProperty(), this::setWidth);
        PropertyListener.register(stage.heightProperty(), this::setHeight);
    }

    private void setX(Number x)
    {
        this.x = x.doubleValue();
    }

    private void setY(Number y)
    {
        this.y = y.doubleValue();
    }

    private void setWidth(Number width)
    {
        this.width = width.doubleValue();
    }

    private void setHeight(Number height)
    {
        this.height = height.doubleValue();
    }

    @Override
    public void restore(Stage stage)
    {
        if (width == 0 || height == 0)
        {
            LOG.debug("State not available, don't restore stage {}: {}", stageId, this);
            return;
        }
        LOG.debug("Restore state for stage id {}: {}", stageId, stage);
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);
    }

    @Override
    public String toString()
    {
        return "StageState [stageId=" + stageId + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height
                + "]";
    }
}
