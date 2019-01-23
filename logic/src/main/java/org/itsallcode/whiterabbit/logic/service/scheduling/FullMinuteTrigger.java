package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.itsallcode.whiterabbit.logic.service.ClockService;

public class FullMinuteTrigger implements Trigger {

	private final ClockService clockService;

	public FullMinuteTrigger(ClockService clockService) {
		this.clockService = clockService;
	}

	@Override
	public Instant nextExecutionTime(TriggerContext context) {
		if (context.lastActualExecutionTime() == null) {
			return clockService.instant();
		}
		return nextFullMinute(context.lastActualExecutionTime());
	}

	private Instant nextFullMinute(Instant lastScheduledExecutionTime) {
		return lastScheduledExecutionTime.plus(1, ChronoUnit.MINUTES) //
				.truncatedTo(ChronoUnit.MINUTES);
	}
}
