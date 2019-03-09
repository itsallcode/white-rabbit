package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Interruption {
    private static final Logger LOG = LogManager.getLogger(Interruption.class);

    private final Consumer<Duration> callback;
    private final ClockService clock;
    private final Instant start;
    private Duration duration;

    private Interruption(ClockService clock, Instant start, Consumer<Duration> callback) {
	this.clock = clock;
	this.start = start;
	this.callback = callback;
	this.duration = null;
    }

    public static Interruption start(ClockService clock, Consumer<Duration> callback) {
	final Instant start = clock.instant();
	LOG.debug("Interruption started at {}", start);
	return new Interruption(clock, start, callback);
    }

    public void end() {
	if (duration != null) {
	    throw new IllegalStateException("Interruption is already finished");
	}
	this.duration = currentDuration();
	LOG.debug("Interruption ended after {}", duration);
	callback.accept(duration);
    }

    public Duration currentDuration() {
	final Instant end = clock.instant();
	return Duration.between(start, end).truncatedTo(ChronoUnit.MINUTES);
    }
}
