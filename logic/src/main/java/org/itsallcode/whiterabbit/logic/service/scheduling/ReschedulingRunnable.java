package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.service.ClockService;

class ReschedulingRunnable extends DelegatingErrorHandlingRunnable implements ScheduledTaskFuture
{
    private static final Logger LOG = LogManager.getLogger(ReschedulingRunnable.class);

    private final Trigger trigger;
    private final ScheduledExecutorService executorService;

    private final Object triggerContextMonitor = new Object();
    private Instant scheduledExecutionTime;
    private Optional<TriggerContext> triggerContext = Optional.empty();
    private final ClockService clock;
    private ScheduledFuture<?> currentFuture;

    ReschedulingRunnable(Runnable command, Trigger trigger, ScheduledExecutorService executorService,
            ClockService clock, ErrorHandler errorHandler)
    {
        super(command, errorHandler);
        this.trigger = trigger;
        this.executorService = executorService;
        this.clock = clock;
    }

    ScheduledTaskFuture schedule()
    {
        synchronized (this.triggerContextMonitor)
        {
            final Instant now = clock.instant();
            this.scheduledExecutionTime = this.trigger.nextExecutionTime(now, this.triggerContext);
            if (this.scheduledExecutionTime == null)
            {
                throw new IllegalStateException("No next execution time");
            }

            final Duration delay = Duration.between(now, this.scheduledExecutionTime);
            if (delay.isNegative())
            {
                throw new IllegalStateException(
                        "Next execution time from trigger " + trigger + " is " + delay + " in the past");
            }
            System.out.println("Schedule " + this + " with delay " + delay);
            if (!executorService.isShutdown())
            {
                this.currentFuture = this.executorService.schedule(this, delay.toMillis(), TimeUnit.MILLISECONDS);
            }
            return this;
        }
    }

    @Override
    public void run()
    {
        final Instant actualExecutionTime = clock.instant();
        super.run();
        final Instant completionTime = clock.instant();
        synchronized (this.triggerContextMonitor)
        {
            Objects.requireNonNull(this.scheduledExecutionTime, "No scheduled execution");
            this.triggerContext = Optional
                    .of(new TriggerContext(this.scheduledExecutionTime, actualExecutionTime, completionTime));
            if (!obtainCurrentFuture().isCancelled())
            {
                schedule();
            }
        }
    }

    private ScheduledFuture<?> obtainCurrentFuture()
    {
        return Objects.requireNonNull(this.currentFuture, "No scheduled future");
    }

    @Override
    public boolean isCancelled()
    {
        synchronized (this.triggerContextMonitor)
        {
            return obtainCurrentFuture().isCancelled();
        }
    }

    @Override
    public boolean isDone()
    {
        synchronized (this.triggerContextMonitor)
        {
            return obtainCurrentFuture().isDone();
        }
    }

    @Override
    public void cancel()
    {
        LOG.debug("Cancel rescheduling runnable");
        synchronized (this.triggerContextMonitor)
        {
            obtainCurrentFuture().cancel(false);
        }
    }
}
