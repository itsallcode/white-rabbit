package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;
import java.util.Optional;

public interface Trigger
{
    Instant nextExecutionTime(Instant now, Optional<TriggerContext> context);
}
