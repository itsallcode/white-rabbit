package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class PeriodicTrigger implements Trigger
{
    private static final Duration ADDITIONAL_WAIT = Duration.ofMillis(50);
    private final ChronoUnit roundToUnit;

    private PeriodicTrigger(ChronoUnit roundToUnit)
    {
        this.roundToUnit = roundToUnit;
    }

    public static Trigger everyMonth()
    {
        return new PeriodicTrigger(ChronoUnit.MONTHS);
    }

    public static Trigger everyDay()
    {
        return new PeriodicTrigger(ChronoUnit.DAYS);
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
        if (context.isEmpty())
        {
            return now;
        }

        return nextFullUnit(now);
    }

    private Instant nextFullUnit(Instant instant)
    {
        return instant.plus(1, roundToUnit) //
                .truncatedTo(roundToUnit) //
                .plus(ADDITIONAL_WAIT);
    }

    @Override
    public String toString()
    {
        return "PeriodicTrigger [roundToUnit=" + roundToUnit + "]";
    }
}
