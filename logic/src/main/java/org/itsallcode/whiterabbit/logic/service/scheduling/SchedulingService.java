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
        final DelegatingErrorHandlingRunnable errorHandlingRunnable = new DelegatingErrorHandlingRunnable(command,
                errorHandler);
        return new ReschedulingRunnable(errorHandlingRunnable, trigger, executorService, clockService).schedule();
    }

    public void schedule(Duration delay, Runnable command)
    {
        final ErrorHandler errorHandler = new DefaultErrorHandler();
        schedule(delay, command, errorHandler);
    }

    public void schedule(Duration delay, Runnable command, final ErrorHandler errorHandler)
    {
        executorService.schedule(new DelegatingErrorHandlingRunnable(command, errorHandler), delay.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void close()
    {
        this.executorService.shutdownNow();
    }
}
