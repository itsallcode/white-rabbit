package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Interruption
{
    private static final Logger LOG = LogManager.getLogger(Interruption.class);

    private final ClockService clock;
    private final Instant start;
    private Duration duration;
    private final InterruptionCallback callback;

    private Interruption(ClockService clock, Instant start, InterruptionCallback callback)
    {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.start = Objects.requireNonNull(start, "start");
        this.callback = Objects.requireNonNull(callback, "callback");
        this.duration = null;
    }

    public static Interruption start(ClockService clock, InterruptionCallback callback)
    {
        final Instant start = clock.instant();
        LOG.debug("Interruption started at {}", start);
        return new Interruption(clock, start, callback);
    }

    public void end()
    {
        if (duration != null)
        {
            throw new IllegalStateException("Interruption is already finished");
        }
        this.duration = currentDuration(clock.instant());
        LOG.debug("Interruption ended after {}", duration);
        callback.addInterruption(duration);
    }

    public void cancel()
    {
        if (duration != null)
        {
            throw new IllegalStateException("Interruption is already finished");
        }
        LOG.debug("Interruption cancelled after {}", currentDuration(clock.instant()));
        callback.cancelInterruption();
    }

    public Instant getStart()
    {
        return start;
    }

    public Duration currentDuration(Instant now)
    {
        return Duration.between(start, now).truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public String toString()
    {
        return "Interruption [start=" + start + ", currently: " + currentDuration(clock.instant()) + ", duration="
                + duration + "]";
    }
}
