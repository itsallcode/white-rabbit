package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;

@FunctionalInterface
public interface Trigger {
	Instant nextExecutionTime(TriggerContext context);
}
