package org.itsallcode.whiterabbit.logic.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.itsallcode.whiterabbit.logic.service.scheduling.DefaultErrorHandler;
import org.itsallcode.whiterabbit.logic.service.scheduling.ErrorHandler;
import org.itsallcode.whiterabbit.logic.service.scheduling.ReschedulingRunnable;
import org.itsallcode.whiterabbit.logic.service.scheduling.ScheduledTaskFuture;
import org.itsallcode.whiterabbit.logic.service.scheduling.Trigger;

public class SchedulingService {

    private final ScheduledExecutorService executorService;
    private final ClockService clockService;
    private final Trigger trigger;

    public SchedulingService(ClockService clockService, Trigger trigger) {
	this(clockService, new ScheduledThreadPoolExecutor(1), trigger);
    }

    SchedulingService(ClockService clockService, ScheduledExecutorService executorService, Trigger trigger) {
	this.clockService = clockService;
	this.executorService = executorService;
	this.trigger = trigger;
    }

    public ScheduledTaskFuture schedule(Runnable command) {
	return schedule(command, trigger);
    }

    private ScheduledTaskFuture schedule(Runnable command, Trigger trigger) {
	final ErrorHandler errorHandler = new DefaultErrorHandler();
	return new ReschedulingRunnable(command, trigger, executorService, clockService, errorHandler).schedule();
    }

    public void shutdown() {
	this.executorService.shutdownNow();
    }
}
