package org.itsallcode.whiterabbit.jfxui.property;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
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

    public ScheduledProperty<Instant> currentInstantProperty()
    {
        return updatingProperty(PeriodicTrigger.everySecond(), () -> appService.getClock().instant());
    }

    public ScheduledProperty<LocalTime> currentMinuteProperty()
    {
        return updatingProperty(PeriodicTrigger.everyMinute(), () -> appService.getClock().getCurrentTime());
    }

    public ScheduledProperty<LocalDate> currentDateProperty()
    {
        return updatingProperty(PeriodicTrigger.everyDay(), () -> appService.getClock().getCurrentDate());
    }

    public ScheduledProperty<YearMonth> currentMonthProperty()
    {
        return updatingProperty(PeriodicTrigger.everyMonth(), () -> appService.getClock().getCurrentYearMonth());
    }

    public <T> ScheduledProperty<T> updatingProperty(Trigger trigger, Supplier<T> supplier)
    {
        final SimpleObjectProperty<T> property = new SimpleObjectProperty<>();
        final T initialValue = supplier.get();
        LOG.debug("Setting initial property value {} from supplier", initialValue);
        property.set(initialValue);
        final Runnable runnable = new ClockPropertyFactoryRunnable<T>(supplier, trigger, property);
        final ScheduledTaskFuture scheduledTaskFuture = appService.schedule(trigger, runnable);
        return new ScheduledProperty<>(property, scheduledTaskFuture);
    }

    private static class ClockPropertyFactoryRunnable<T> implements Runnable
    {
        private final Supplier<T> supplier;
        private final Trigger trigger;
        private final SimpleObjectProperty<T> property;

        private ClockPropertyFactoryRunnable(Supplier<T> supplier, Trigger trigger, SimpleObjectProperty<T> property)
        {
            this.supplier = supplier;
            this.trigger = trigger;
            this.property = property;
        }

        @Override
        public void run()
        {
            JavaFxUtil.runOnFxApplicationThread(() -> {
                final T newValue = supplier.get();
                property.set(newValue);
            });
        }

        @Override
        public String toString()
        {
            return "ClockPropertyFactoryRunnable [trigger=" + trigger + ", supplier=" + supplier + ", property="
                    + property + "]";
        }
    }
}
