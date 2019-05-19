package org.itsallcode.whiterabbit.jfxui.property;

import org.itsallcode.whiterabbit.logic.service.scheduling.ScheduledTaskFuture;

import javafx.beans.property.Property;

public class ScheduledProperty<T>
{
    private final Property<T> property;
    private final ScheduledTaskFuture scheduledTaskFuture;

    ScheduledProperty(Property<T> property, ScheduledTaskFuture scheduledTaskFuture)
    {
        this.property = property;
        this.scheduledTaskFuture = scheduledTaskFuture;
    }

    public Property<T> property()
    {
        return property;
    }

    public void cancel()
    {
        scheduledTaskFuture.cancel();
    }
}
