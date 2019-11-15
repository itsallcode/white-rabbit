package org.itsallcode.whiterabbit.jfxui.property;

import java.time.Instant;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.scheduling.PeriodicTrigger;
import org.itsallcode.whiterabbit.logic.service.scheduling.ScheduledTaskFuture;
import org.itsallcode.whiterabbit.logic.service.scheduling.Trigger;

import javafx.beans.property.SimpleObjectProperty;

public class ClockPropertyFactory
{
    private static final Logger LOG = LogManager.getLogger(ClockPropertyFactory.class);

    private final AppService appService;

    public ClockPropertyFactory(AppService appService)
    {
        this.appService = appService;
    }

    public ScheduledProperty<Instant> currentTimeProperty()
    {
        return updatingProperty(PeriodicTrigger.everySecond(), () -> appService.getClock().instant());
    }

    public <T> ScheduledProperty<T> updatingProperty(Trigger trigger, Supplier<T> supplier)
    {
        final SimpleObjectProperty<T> property = new SimpleObjectProperty<>();
        final T initialValue = supplier.get();
        LOG.debug("Setting initial property value {} from supplier", initialValue);
        property.set(initialValue);
        final ScheduledTaskFuture scheduledTaskFuture = appService.schedule(trigger,
                () -> JavaFxUtil.runOnFxApplicationThread(() -> {
                    final T newValue = supplier.get();
                    property.set(newValue);
                }));

        return new ScheduledProperty<>(property, scheduledTaskFuture);
    }
}
