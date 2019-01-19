package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Duration;
import java.time.Instant;

import org.itsallcode.whiterabbit.logic.service.ClockService;

public class PeriodicTrigger implements Trigger {

	private final ClockService clockService;
	private final Duration delay;

	public PeriodicTrigger(ClockService clockService, Duration delay) {
		this.clockService = clockService;
		this.delay = delay;
	}

	@Override
	public Instant nextExecutionTime(TriggerContext context) {
		if (context.lastScheduledExecutionTime() == null) {
			return clockService.instant();
		}
		return context.lastScheduledExecutionTime().plus(delay);
	}
}
