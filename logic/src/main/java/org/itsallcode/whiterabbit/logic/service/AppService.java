package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicReference;
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

public class AppService
{
    private static final Duration AUTO_INTERRUPTION_THRESHOLD = Duration.ofMinutes(2);

    private static final Logger LOG = LogManager.getLogger(AppService.class);

    private final Storage storage;
    private final ClockService clock;
    private final FormatterService formatterService;
    private final SchedulingService schedulingService;
    private final AtomicReference<Interruption> currentInterruption = new AtomicReference<>();

    public AppService(Storage storage, FormatterService formatterService, ClockService clock,
            SchedulingService schedulingService)
    {
        this.storage = storage;
        this.formatterService = formatterService;
        this.clock = clock;
        this.schedulingService = schedulingService;
    }

    public static AppService create(final Config config, final FormatterService formatterService)
    {
        final Storage storage = new Storage(new DateToFileMapper(config.getDataDir()));
        final ClockService clockService = new ClockService();
        final Trigger trigger = new FullMinuteTrigger();
        final SchedulingService schedulingService = new SchedulingService(clockService, trigger);
        return new AppService(storage, formatterService, clockService, schedulingService);
    }

    public ScheduledTaskFuture startAutoUpdate(Consumer<DayRecord> listener)
    {
        return this.schedulingService.schedule(new DayUpdateExecutor(this, listener));
    }

    public DayRecord updateNow()
    {
        final LocalDate today = clock.getCurrentDate();
        final MonthIndex month = storage.loadMonth(today);
        final DayRecord day = month.getDay(today);
        final LocalTime now = clock.getCurrentTime();
        if (day.isWorkingDay())
        {
            boolean updated = false;
            if (shouldUpdateBegin(day, now))
            {
                day.setBegin(now);
                updated = true;
            }
            if (shouldUpdateEnd(day, now))
            {
                day.setEnd(now);
                updated = true;
            }
            if (updated)
            {
                LOG.trace("Updating day {} for time {}: {}", day.getDate(), now,
                        formatterService.format(day));
                storage.storeMonth(month);
            }
            else
            {
                LOG.trace("No update for {} at {}: {}", day.getDate(), now);
            }
        }
        else
        {
            LOG.trace("Today {} is a {}, no update required", day.getDate(), day.getType());
        }
        return day;
    }

    private boolean shouldUpdateBegin(final DayRecord day, final LocalTime now)
    {
        return day.getBegin() == null || day.getBegin().isAfter(now);
    }

    private boolean shouldUpdateEnd(final DayRecord day, final LocalTime now)
    {
        if (day.getEnd() == null)
        {
            return true;
        }
        if (day.getEnd().isAfter(now))
        {
            return false;
        }
        final Duration interruption = Duration.between(day.getEnd(), now);
        if (interruption.minus(AUTO_INTERRUPTION_THRESHOLD).isNegative())
        {
            return true;
        }
        if (!isInterruptionActive())
        {
            final Duration interruptionToAdd = Duration.between(day.getEnd(),
                    clock.getCurrentTime());
            addToInterruption(day, interruptionToAdd);
        }
        return true;
    }

    public void report()
    {
        final DayReporter reporter = new DayReporter(formatterService);
        storage.loadAll().getDays().forEach(reporter::add);
        reporter.finish();
    }

    public Interruption startInterruption()
    {
        final Interruption newInterruption = Interruption.start(clock, this::addToInterruption);
        if (currentInterruption.compareAndSet(null, newInterruption))
        {
            return newInterruption;
        }
        else
        {
            throw new IllegalStateException(
                    "An interruption was already started: " + currentInterruption.get());
        }
    }

    private boolean isInterruptionActive()
    {
        return currentInterruption.get() != null;
    }

    private void addToInterruption(Duration additionalInterruption)
    {
        if (additionalInterruption.isZero())
        {
            LOG.debug("Interruption is zero: ignore.");
            return;
        }
        final LocalDate today = clock.getCurrentDate();
        final MonthIndex month = storage.loadMonth(today);
        final DayRecord day = month.getDay(today);
        addToInterruption(day, additionalInterruption);
        storage.storeMonth(month);
    }

    private void addToInterruption(final DayRecord day, Duration additionalInterruption)
    {
        final Duration updatedInterruption = day.getInterruption().plus(additionalInterruption);
        LOG.info("Add interruption {} for {}, total interruption: {}: {}", additionalInterruption,
                day.getDate(), updatedInterruption, formatterService.format(day));
        day.setInterruption(updatedInterruption);
    }

    public void shutdown()
    {
        LOG.debug("Shutting down...");
        this.schedulingService.shutdown();
    }

    public ClockService getClock()
    {
        return clock;
    }
}
