package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.service.ClockService;

public class FullMinuteTrigger implements Trigger
{

    private final ClockService clockService;

    public FullMinuteTrigger(ClockService clockService)
    {
        this.clockService = clockService;
    }

    @Override
    public Instant nextExecutionTime(Optional<TriggerContext> context)
    {
        if (!context.isPresent())
        {
            return clockService.instant();
        }
        return nextFullMinute(context.get().lastActualExecutionTime());
    }

    private Instant nextFullMinute(Instant lastScheduledExecutionTime)
    {
        return lastScheduledExecutionTime.plus(1, ChronoUnit.MINUTES) //
                .truncatedTo(ChronoUnit.MINUTES);
    }
}
