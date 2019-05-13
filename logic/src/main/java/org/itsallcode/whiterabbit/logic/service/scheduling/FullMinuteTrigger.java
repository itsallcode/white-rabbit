package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FullMinuteTrigger implements Trigger
{
    private static final Logger LOG = LogManager.getLogger(FullMinuteTrigger.class);
    private static final Duration MINIMAL_DURATION_BETWEEN_EXECUTIONS = Duration.ofMillis(10);

    @Override
    public Instant nextExecutionTime(Instant now, Optional<TriggerContext> context)
    {
        if (!context.isPresent())
        {
            return now;
        }

        final Instant nextFullMinute = nextFullMinute(now);
        final Duration durationBetweenLastAndNextExecution = Duration
                .between(context.get().lastCompletionTime(), nextFullMinute);
        if (durationBetweenLastAndNextExecution.minus(MINIMAL_DURATION_BETWEEN_EXECUTIONS)
                .isNegative())
        {
            final Instant nextNextFullMinute = nextFullMinute(nextFullMinute);
            LOG.warn(
                    "Duration between last execution {} and next full minute {} is less than {}: ");
            return nextNextFullMinute;
        }
        return nextFullMinute;
    }

    private Instant nextFullMinute(Instant instant)
    {
        return instant.plus(1, ChronoUnit.MINUTES) //
                .truncatedTo(ChronoUnit.MINUTES);
    }
}
