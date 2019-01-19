package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.service.ClockService;

public class ReschedulingRunnable extends DelegatingErrorHandlingRunnable implements ScheduledTaskFuture {

	private static final Logger LOG = LogManager.getLogger(ReschedulingRunnable.class);

	private final Trigger trigger;
	private final ScheduledExecutorService executorService;

	private final Object triggerContextMonitor = new Object();
	private Instant scheduledExecutionTime;
	private final TriggerContext triggerContext = new TriggerContext();
	private final ClockService clock;
	private ScheduledFuture<?> currentFuture;

	public ReschedulingRunnable(Runnable command, Trigger trigger, ScheduledExecutorService executorService, ClockService clock, ErrorHandler errorHandler) {
		super(command, errorHandler);
		this.trigger = trigger;
		this.executorService = executorService;
		this.clock = clock;
	}

	public ScheduledTaskFuture schedule() {
		synchronized (this.triggerContextMonitor) {
			this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
			if (this.scheduledExecutionTime == null) {
				return null;
			}

			final Duration initialDelay = Duration.between(clock.instant(), this.scheduledExecutionTime);
			LOG.trace("Schedule next execution at {} in {}", this.scheduledExecutionTime, initialDelay);
			this.currentFuture = this.executorService.schedule(this, initialDelay.toMillis(), TimeUnit.MILLISECONDS);
			return this;
		}
	}

	@Override
	public void run() {
		final Instant actualExecutionTime = clock.instant();
		super.run();
		final Instant completionTime = clock.instant();
		synchronized (this.triggerContextMonitor) {
			Objects.requireNonNull(this.scheduledExecutionTime, "No scheduled execution");
			this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
			if (!obtainCurrentFuture().isCancelled()) {
				schedule();
			}
		}
	}

	private ScheduledFuture<?> obtainCurrentFuture() {
		return Objects.requireNonNull(this.currentFuture, "No scheduled future");
	}

	@Override
	public boolean isCancelled() {
		synchronized (this.triggerContextMonitor) {
			return obtainCurrentFuture().isCancelled();
		}
	}

	@Override
	public boolean isDone() {
		synchronized (this.triggerContextMonitor) {
			return obtainCurrentFuture().isDone();
		}
	}

	@Override
	public void cancel() {
		synchronized (this.triggerContextMonitor) {
			obtainCurrentFuture().cancel(false);
		}
	}
}
