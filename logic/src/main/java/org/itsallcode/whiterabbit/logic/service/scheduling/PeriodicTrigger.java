package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class PeriodicTrigger implements Trigger
{
    private final Duration delay;

    public PeriodicTrigger(Duration delay)
    {
        this.delay = delay;
    }

    @Override
    public Instant nextExecutionTime(Instant now, Optional<TriggerContext> context)
    {
        if (!context.isPresent())
        {
            return now;
        }
        return context.get().lastScheduledExecutionTime().plus(delay);
    }
}
