package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.SchedulingService.ScheduledTaskFuture;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class AppService {
	private static final Logger LOG = LogManager.getLogger(AppService.class);

	private final Storage storage;
	private final ClockService clock;
	private final FormatterService formatterService;

	private final SchedulingService schedulingService;

	public AppService(Storage storage, FormatterService formatterService, ClockService clock, SchedulingService schedulingService) {
		this.storage = storage;
		this.formatterService = formatterService;
		this.clock = clock;
		this.schedulingService = schedulingService;
	}

	public ScheduledTaskFuture startAutoUpdate(Consumer<DayRecord> listener) {
		return this.schedulingService.schedule(new DayUpdateExecutor(this, listener));
	}

	public DayRecord updateNow() {
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
				LOG.info("Updating day {} for time {}\n{}", today, now, formatterService.format(day));
				storage.storeMonth(month);
			} else {
				LOG.info("No update for {} at {}\n{}", today, now, formatterService.format(day));
			}
		} else {
			LOG.info("Today {} is a {}, no update required\n{}", today, day.getType(), formatterService.format(day));
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
		LOG.info("Add interruption {} for {}, total interruption: {}\n{}", additionalInterruption, today, totalInterruption, formatterService.format(day));
		day.setInterruption(totalInterruption);
		storage.storeMonth(month);
	}

	public void report() {
		LOG.info("Reporting...");
		final DayReporter reporter = new DayReporter(formatterService);
		storage.loadAll().getDays().forEach(reporter::add);
		reporter.finish();
	}

	public Interruption startInterruption() {
		return new Interruption(clock, this::incrementInterruption);
	}
}
