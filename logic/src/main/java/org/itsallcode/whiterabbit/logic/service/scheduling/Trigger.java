package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;
import java.util.Optional;

@FunctionalInterface
public interface Trigger
{
    Instant nextExecutionTime(Optional<TriggerContext> context);
}
