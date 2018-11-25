package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SchedulingService {
	private static final Logger LOG = LogManager.getLogger(SchedulingService.class);

	private final ScheduledExecutorService executorService;
	private final ClockService clockService;

	public SchedulingService(ClockService clockService) {
		this(clockService, new ScheduledThreadPoolExecutor(1));
	}

	SchedulingService(ClockService clockService, ScheduledExecutorService executorService) {
		this.clockService = clockService;
		this.executorService = executorService;
	}

	public ScheduledTaskFuture schedule(Runnable command) {
		final Duration initialDelay = clockService.getDurationUntilNextFullMinute();
		final Duration period = Duration.ofMinutes(1);
		LOG.info("Scheduling task with initial delay {} and period {}", initialDelay, period);
		return schedule(command, initialDelay, period);
	}

	private ScheduledTaskFuture schedule(Runnable command, Duration initialDelay, Duration period) {
		final ScheduledFuture<?> future = executorService.scheduleAtFixedRate(command, initialDelay.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS);
		return new ScheduledTaskFuture(future);
	}

	public static class ScheduledTaskFuture {
		private final ScheduledFuture<?> future;

		public ScheduledTaskFuture(ScheduledFuture<?> future) {
			this.future = future;
		}

		public void cancel() {
			LOG.info("Cancelling scheduled task");
			final boolean success = this.future.cancel(false);
			if (!success) {
				LOG.warn("Error cancelling the task");
			}
		}

		public boolean isDone() {
			return this.future.isDone();
		}
	}
}
