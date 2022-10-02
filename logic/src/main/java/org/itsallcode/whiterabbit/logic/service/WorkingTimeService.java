package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.AppServiceCallback.InterruptionDetectedDecision;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class WorkingTimeService
{
    private static final Duration AUTO_INTERRUPTION_THRESHOLD = Duration.ofMinutes(3);

    private static final Logger LOG = LogManager.getLogger(WorkingTimeService.class);

    private final AtomicReference<Interruption> currentInterruption = new AtomicReference<>();
    private final AtomicReference<LocalDate> workStoppedForToday = new AtomicReference<>();
    private final Storage storage;
    private final ClockService clock;
    private final AppServiceCallback appServiceCallback;

    public WorkingTimeService(Storage storage, ClockService clock, AppServiceCallback appServiceCallback)
    {
        this.storage = storage;
        this.clock = clock;
        this.appServiceCallback = appServiceCallback;
    }

    public void updateNow()
    {
        final LocalDate today = clock.getCurrentDate();
        if (workStoppedForToday(today))
        {
            LOG.trace("Work stopped for today on {}, skipping update", today);
            return;
        }
        final MonthIndex month = storage.loadOrCreate(YearMonth.from(today));
        final DayRecord day = month.getDay(today);
        final LocalTime now = clock.getCurrentTime();
        if (updateDay(day, now))
        {
            LOG.debug("Day {} updated at {}", day.getDate(), now);
            storage.storeMonth(month);
            appServiceCallback.recordUpdated(day);
        }
        else
        {
            LOG.trace("No update for {} at {}", day.getDate(), now);
        }
    }

    private boolean updateDay(final DayRecord day, final LocalTime now)
    {
        if (!day.getType().isWorkDay())
        {
            LOG.trace("Today {} is a {}, no update required", day.getDate(), day.getType());
            return false;
        }
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
        return updated;
    }

    private boolean workStoppedForToday(LocalDate today)
    {
        if (today.equals(workStoppedForToday.get()))
        {
            return true;
        }
        if (workStoppedForToday.get() != null)
        {
            appServiceCallback.workStoppedForToday(false);
        }
        workStoppedForToday.set(null);
        return false;
    }

    public void toggleStopWorkForToday()
    {
        final LocalDate today = clock.getCurrentDate();
        if (workStoppedForToday(today))
        {
            LOG.debug("Continue work for today");
            this.workStoppedForToday.set(null);
            appServiceCallback.workStoppedForToday(false);
            updateNow();
        }
        else
        {
            stopWorkForToday();
        }
    }

    private void stopWorkForToday()
    {
        final LocalDate today = clock.getCurrentDate();
        if (workStoppedForToday(today))
        {
            LOG.debug("Work already stopped for today {}: ignore", today);
            return;
        }
        LOG.debug("Stopping work for today {}", today);
        this.workStoppedForToday.set(today);
        appServiceCallback.workStoppedForToday(true);
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
        if (isInterruptionActive())
        {
            return true;
        }
        return automaticInterruptionDetected(day, now);
    }

    private boolean automaticInterruptionDetected(final DayRecord day, final LocalTime now)
    {
        final Duration interruptionToAdd = Duration.between(day.getEnd(), now);
        LOG.debug("Interruption of {} detected", interruptionToAdd);
        final InterruptionDetectedDecision decision = appServiceCallback.automaticInterruptionDetected(day.getEnd(),
                interruptionToAdd);
        switch (decision)
        {
        case ADD_INTERRUPTION:
            addInterruption(day, interruptionToAdd);
            return true;
        case SKIP_INTERRUPTION:
            return true;
        case STOP_WORKING_FOR_TODAY:
            stopWorkForToday();
            return false;
        default:
            throw new IllegalStateException("Unknown enum value " + decision);
        }
    }

    private boolean isInterruptionActive()
    {
        return currentInterruption.get() != null;
    }

    public Interruption startInterruption()
    {
        final Interruption newInterruption = Interruption.start(clock,
                InterruptionCallback.create(this::addToInterruption, this::cancelInterruption));
        if (currentInterruption.compareAndSet(null, newInterruption))
        {
            return newInterruption;
        }
        else
        {
            throw new IllegalStateException("An interruption was already started: " + currentInterruption.get());
        }
    }

    private void addToInterruption(Duration additionalInterruption)
    {
        resetInterruption();

        if (additionalInterruption.isZero())
        {
            LOG.debug("Interruption is zero: ignore.");
            return;
        }
        final LocalDate today = clock.getCurrentDate();
        addInterruption(today, additionalInterruption);
    }

    public void addInterruption(final LocalDate today, Duration interruption)
    {
        final MonthIndex month = storage.loadOrCreate(YearMonth.from(today));
        final DayRecord day = month.getDay(today);
        addInterruption(day, interruption);
        day.setEnd(clock.getCurrentTime());
        storage.storeMonth(month);
        appServiceCallback.recordUpdated(day);
    }

    private void resetInterruption()
    {
        if (!currentInterruption.compareAndSet(currentInterruption.get(), null))
        {
            throw new IllegalStateException("No interruption is active");
        }
    }

    private void cancelInterruption()
    {
        resetInterruption();
    }

    private void addInterruption(final DayRecord day, Duration additionalInterruption)
    {
        final Duration updatedInterruption = day.getInterruption().plus(additionalInterruption);
        LOG.info("Add interruption {} for {}, total interruption: {}", additionalInterruption, day.getDate(),
                updatedInterruption);
        day.setInterruption(updatedInterruption);
    }
}
