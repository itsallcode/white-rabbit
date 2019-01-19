package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.scheduling.FullMinuteTrigger;
import org.itsallcode.whiterabbit.logic.service.scheduling.ScheduledTaskFuture;
import org.itsallcode.whiterabbit.logic.service.scheduling.Trigger;
import org.itsallcode.whiterabbit.logic.storage.DateToFileMapper;
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

	public static AppService create(final Config config, final FormatterService formatterService) {
		final Storage storage = new Storage(new DateToFileMapper(config.getDataDir()));
		final ClockService clockService = new ClockService();
		final Trigger trigger = new FullMinuteTrigger(clockService);
		final SchedulingService schedulingService = new SchedulingService(clockService, trigger);
		return new AppService(storage, formatterService, clockService, schedulingService);
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
				LOG.info("Updating day {} for time {}\n{}", day.getDate(), now, formatterService.format(day));
				storage.storeMonth(month);
			} else {
				LOG.trace("No update for {} at {}\n{}", day.getDate(), now);
			}
		} else {
			LOG.trace("Today {} is a {}, no update required", day.getDate(), day.getType());
		}
		return day;
	}

	public void report() {
		LOG.debug("Reporting...");
		final DayReporter reporter = new DayReporter(formatterService);
		storage.loadAll().getDays().forEach(reporter::add);
		reporter.finish();
	}

	public Interruption startInterruption() {
		return Interruption.start(clock, this::addToInterruption);
	}

	private void addToInterruption(Duration additionalInterruption) {
		if (additionalInterruption.isZero()) {
			LOG.debug("Interruption is zero: ignore.");
			return;
		}
		final LocalDate today = clock.getCurrentDate();
		final MonthIndex month = storage.loadMonth(today);
		final DayRecord day = month.getDay(today);
		final Duration updatedInterruption = day.getInterruption().plus(additionalInterruption);
		LOG.info("Add interruption {} for {}, total interruption: {}\n{}", additionalInterruption, day.getDate(), updatedInterruption,
				formatterService.format(day));
		day.setInterruption(updatedInterruption);
		storage.storeMonth(month);
	}

	public void shutdown() {
		LOG.debug("Shutting down...");
		this.schedulingService.shutdown();
	}
}
