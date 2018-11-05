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

	Interruption(ClockService clock, Consumer<Duration> callback) {
		this.clock = clock;
		this.callback = callback;
		this.start = clock.instant();
		LOG.info("Interruption started at {}", start);
		this.duration = null;
	}

	public void end() {
		if (duration != null) {
			throw new IllegalStateException("Interruption is already finished");
		}
		this.duration = currentDuration();
		LOG.info("Interruption ended after {}", duration);
		callback.accept(duration);
	}

	public Duration currentDuration() {
		final Instant end = clock.instant();
		return Duration.between(start, end).truncatedTo(ChronoUnit.MINUTES);
	}
}
