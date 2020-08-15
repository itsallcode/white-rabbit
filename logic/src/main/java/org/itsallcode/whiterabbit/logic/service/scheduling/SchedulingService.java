package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.util.concurrent.ScheduledExecutorService;

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

    @Override
    public void close()
    {
        this.executorService.shutdownNow();
    }
}
