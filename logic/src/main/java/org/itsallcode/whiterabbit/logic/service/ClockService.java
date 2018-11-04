package org.itsallcode.whiterabbit.logic.service;

import java.time.Clock;
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
		return LocalTime.now(clock).truncatedTo(ChronoUnit.MINUTES);
	}
}
