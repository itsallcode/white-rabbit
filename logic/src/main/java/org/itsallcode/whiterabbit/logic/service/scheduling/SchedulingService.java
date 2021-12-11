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
        final ErrorHandler errorHandler = new LoggingErrorHandler();
        final Runnable errorHandlingRunnable = new ErrorHandlingRunnable(command,
                errorHandler);
        final Runnable otherThreadRunnable = new OtherThreadRunnable(executorService, errorHandlingRunnable);
        return new ReschedulingRunnable(otherThreadRunnable, trigger, executorService, clockService).schedule();
    }

    public void schedule(Duration delay, Runnable command)
    {
        final ErrorHandler errorHandler = new LoggingErrorHandler();
        schedule(delay, command, errorHandler);
    }

    public void schedule(Duration delay, Runnable command, final ErrorHandler errorHandler)
    {
        executorService.schedule(new ErrorHandlingRunnable(command, errorHandler), delay.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void close()
    {
        this.executorService.shutdownNow();
    }
}
