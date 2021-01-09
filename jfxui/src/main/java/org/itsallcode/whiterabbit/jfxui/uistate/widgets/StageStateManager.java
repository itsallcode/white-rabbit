package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.property.DelayedPropertyListener;
import org.itsallcode.whiterabbit.jfxui.uistate.model.StageStateModel;

import javafx.stage.Stage;

class StageStateManager implements WidgetStateManager<Stage, StageStateModel>
{
    private static final Logger LOG = LogManager.getLogger(StageStateManager.class);
    private final DelayedPropertyListener propertyListener;

    public StageStateManager(DelayedPropertyListener propertyListener)
    {
        super();
        this.propertyListener = propertyListener;
    }

    @Override
    public void restore(Stage widget, StageStateModel model)
    {
        if (model.width == 0 || model.height == 0)
        {
            LOG.debug("State not available, don't restore stage {}: {}", model.id, this);
            return;
        }
        widget.setX(model.x);
        widget.setY(model.y);
        widget.setWidth(model.width);
        widget.setHeight(model.height);
    }

    @Override
    public void watch(Stage widget, StageStateModel model)
    {
        propertyListener.register(widget.xProperty(), model::setX);
        propertyListener.register(widget.yProperty(), model::setY);
        propertyListener.register(widget.widthProperty(), model::setWidth);
        propertyListener.register(widget.heightProperty(), model::setHeight);
    }

    @Override
    public StageStateModel createEmptyModel(String id)
    {
        return new StageStateModel(id);
    }
}
