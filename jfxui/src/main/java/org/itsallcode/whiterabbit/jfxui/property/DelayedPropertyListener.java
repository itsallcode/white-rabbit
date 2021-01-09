package org.itsallcode.whiterabbit.jfxui.property;

import java.time.Duration;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.service.scheduling.SchedulingService;

import javafx.beans.value.ObservableValue;

public class DelayedPropertyListener
{
    private static final Logger LOG = LogManager.getLogger(DelayedPropertyListener.class);
    public static final Duration DELAY = Duration.ofMillis(500);

    private final SchedulingService schedulingService;

    public DelayedPropertyListener(SchedulingService schedulingService)
    {
        this.schedulingService = schedulingService;
    }

    public <T> void register(ObservableValue<T> property, Consumer<T> delegate)
    {
        delegate.accept(property.getValue());
        property.addListener(observable -> {
            LOG.trace("Schedule with {} delay", DELAY);
            schedulingService.schedule(DELAY, () -> {
                final T newValue = property.getValue();
                LOG.trace("Property {} changed to {}", property, newValue);
                delegate.accept(newValue);
            });
        });
    }
}
