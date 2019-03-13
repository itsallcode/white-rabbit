package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class FullMinuteTrigger implements Trigger
{
    @Override
    public Instant nextExecutionTime(Instant now, Optional<TriggerContext> context)
    {
        if (!context.isPresent())
        {
            return now;
        }
        return nextFullMinute(now);
    }

    private Instant nextFullMinute(Instant instant)
    {
        return instant.plus(1, ChronoUnit.MINUTES) //
                .truncatedTo(ChronoUnit.MINUTES);
    }
}
