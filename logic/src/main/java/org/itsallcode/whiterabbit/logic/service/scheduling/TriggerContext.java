package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;

public class TriggerContext {
	private Instant lastScheduledExecutionTime;
	private Instant lastActualExecutionTime;
	private Instant lastCompletionTime;

	public TriggerContext() {
	}

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

	public void update(Instant scheduledExecutionTime, Instant actualExecutionTime, Instant completionTime) {
		this.lastScheduledExecutionTime = scheduledExecutionTime;
		this.lastActualExecutionTime = actualExecutionTime;
		this.lastCompletionTime = completionTime;
	}
}
