package org.itsallcode.whiterabbit.logic.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ClockService {
    private final Clock clock;

    ClockService(Clock clock) {
	this.clock = clock;
    }

    public ClockService() {
	this(Clock.systemDefaultZone());
    }

    public LocalDate getCurrentDate() {
	return LocalDate.now(clock);
    }

    public LocalTime getCurrentTime() {
	return getExactCurrentTime().truncatedTo(ChronoUnit.MINUTES);
    }

    private LocalTime getExactCurrentTime() {
	return LocalTime.now(clock);
    }

    public Duration getDurationUntilNextFullMinute() {
	final LocalTime now = getExactCurrentTime();
	final LocalTime nextFullMinute = now.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
	return Duration.between(now, nextFullMinute);
    }

    public Instant instant() {
	return clock.instant();
    }
}
