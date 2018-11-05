package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class AppService {
	private static final Logger LOG = LogManager.getLogger(AppService.class);

	private final Storage storage;
	private final ClockService clock;

	public AppService(Storage storage, ClockService clock) {
		this.storage = storage;
		this.clock = clock;
	}

	public DayRecord update() {
		final LocalDate today = clock.getCurrentDate();
		final MonthIndex month = storage.loadMonth(today);
		final DayRecord day = month.getDay(today);
		final LocalTime now = clock.getCurrentTime();
		if (day.isWorkingDay()) {
			boolean updated = false;
			if (day.getBegin() == null || day.getBegin().isAfter(now)) {
				day.setBegin(now);
				updated = true;
			}
			if (day.getEnd() == null || day.getEnd().isBefore(now)) {
				day.setEnd(now);
				updated = true;
			}
			if (updated) {
				LOG.info("Updating day {} for time {}", today, now);
				storage.storeMonth(month);
			} else {
				LOG.info("No update for {} at {}", today, now);
			}
		} else {
			LOG.info("Today {} is a {}, no update required", today, day.getType());
		}
		return day;
	}

	public void incrementInterruption(Duration additionalInterruption) {
		if (additionalInterruption.isZero()) {
			LOG.info("Interruption is zero: ignore.");
			return;
		}
		final LocalDate today = clock.getCurrentDate();
		final MonthIndex month = storage.loadMonth(today);
		final DayRecord day = month.getDay(today);
		final Duration totalInterruption = day.getInterruption().plus(additionalInterruption);
		LOG.info("Add interruption {} for {}, total interruption: {}", additionalInterruption, today, totalInterruption);
		day.setInterruption(totalInterruption);
		storage.storeMonth(month);
	}

	public void report() {
		LOG.info("Reporting...");
		final DayReporter reporter = new DayReporter();
		storage.loadAll().getDays().forEach(reporter::add);
		reporter.finish();
	}

	public Interruption startInterruption() {
		return new Interruption(clock, this::incrementInterruption);
	}
}
