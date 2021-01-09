package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.itsallcode.whiterabbit.logic.service.ClockService;

public class SchedulingService implements AutoCloseable
{
    private final ScheduledExecutorService executorService;
    private final ClockService clockService;

    public SchedulingService(ClockService clockService, ScheduledExecutorService executorService)
    {
        this.clockService = clockService;
        this.executorService = executorService;
    }

    public ScheduledTaskFuture schedule(Trigger trigger, Runnable command)
    {
        final ErrorHandler errorHandler = new DefaultErrorHandler();
        return new ReschedulingRunnable(command, trigger, executorService, clockService, errorHandler).schedule();
    }

    public void schedule(Duration delay, Runnable command)
    {
        final ErrorHandler errorHandler = new DefaultErrorHandler();
        executorService.schedule(new DelegatingErrorHandlingRunnable(command, errorHandler), delay.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void close()
    {
        this.executorService.shutdownNow();
    }
}
