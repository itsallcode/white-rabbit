package org.itsallcode.whiterabbit.logic.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.itsallcode.whiterabbit.logic.service.scheduling.DefaultErrorHandler;
import org.itsallcode.whiterabbit.logic.service.scheduling.ErrorHandler;
import org.itsallcode.whiterabbit.logic.service.scheduling.ReschedulingRunnable;
import org.itsallcode.whiterabbit.logic.service.scheduling.ScheduledTaskFuture;
import org.itsallcode.whiterabbit.logic.service.scheduling.Trigger;

public class SchedulingService implements AutoCloseable
{
    private final ScheduledExecutorService executorService;
    private final ClockService clockService;

    public SchedulingService(ClockService clockService)
    {
        this(clockService, new ScheduledThreadPoolExecutor(1));
    }

    SchedulingService(ClockService clockService, ScheduledExecutorService executorService)
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
