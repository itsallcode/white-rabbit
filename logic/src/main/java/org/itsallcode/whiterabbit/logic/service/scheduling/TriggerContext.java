package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;

public class TriggerContext {
	private final Instant lastScheduledExecutionTime;
	private final Instant lastActualExecutionTime;
	private final Instant lastCompletionTime;

	public TriggerContext(Instant lastScheduledExecutionTime, Instant lastActualExecutionTime, Instant lastCompletionTime) {
		this.lastScheduledExecutionTime = lastScheduledExecutionTime;
		this.lastActualExecutionTime = lastActualExecutionTime;
		this.lastCompletionTime = lastCompletionTime;
	}

	public Instant lastScheduledExecutionTime() {
		return lastScheduledExecutionTime;
	}

	public Instant lastCompletionTime() {
		return lastCompletionTime;
	}

	public Instant lastActualExecutionTime() {
		return lastActualExecutionTime;
	}
}
