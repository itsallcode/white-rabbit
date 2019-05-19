package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PeriodicTrigger implements Trigger
{
    private static final Logger LOG = LogManager.getLogger(PeriodicTrigger.class);

    private final ChronoUnit roundToUnit;

    private PeriodicTrigger(ChronoUnit roundToUnit)
    {
        this.roundToUnit = roundToUnit;
    }

    public static Trigger everyMinute()
    {
        return new PeriodicTrigger(ChronoUnit.MINUTES);
    }

    public static Trigger everySecond()
    {
        return new PeriodicTrigger(ChronoUnit.SECONDS);
    }

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
        return instant.plus(1, roundToUnit) //
                .truncatedTo(roundToUnit);
    }
}
