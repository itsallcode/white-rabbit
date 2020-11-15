package org.itsallcode.whiterabbit.jfxui.testutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.ArgumentCaptor;

public class TimeUtil
{
    private static final Logger LOG = LogManager.getLogger(TimeUtil.class);
    private final Clock clockMock;
    private final ScheduledExecutorService executorServiceMock;

    private Runnable updateEverySecondRunnable;
    private Runnable updateEveryMinuteRunnable;
    private Runnable updateEveryDayRunnable;

    private TimeUtil(Clock clockMock, ScheduledExecutorService executorServiceMock)
    {
        this.clockMock = clockMock;
        this.executorServiceMock = executorServiceMock;
    }

    public static TimeUtil start(Instant initialTime)
    {
        final Clock clockMock = mock(Clock.class);
        final ScheduledExecutorService executorServiceMock = mock(ScheduledExecutorService.class);
        final ScheduledFuture<?> scheduledFutureMock = mock(ScheduledFuture.class);

        when(executorServiceMock.schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS)))
                .thenAnswer(invocation -> {
                    final Long delayMillis = invocation.getArgument(1, Long.class);
                    final Runnable runnable = invocation.getArgument(0, Runnable.class);
                    LOG.trace("Mock scheduler called: {} -> {}", Duration.ofMillis(delayMillis), runnable);
                    return scheduledFutureMock;
                });

        when(clockMock.getZone()).thenReturn(ZoneId.of("Europe/Berlin"));
        when(clockMock.instant()).thenReturn(initialTime);

        return new TimeUtil(clockMock, executorServiceMock);
    }

    public LocalDate getCurrentDate()
    {
        return LocalDate.ofInstant(clock().instant(), clock().getZone());
    }

    public LocalTime getCurrentTimeMinutes()
    {
        return LocalTime.ofInstant(clock().instant(), clock().getZone()).truncatedTo(ChronoUnit.MINUTES);
    }

    public LocalTime getCurrentTimeSeconds()
    {
        return LocalTime.ofInstant(clock().instant(), clock().getZone()).truncatedTo(ChronoUnit.SECONDS);
    }

    public int getCurrentDayRowIndex()
    {
        return getCurrentDate().getDayOfMonth() - 1;
    }

    public void tickSecond()
    {
        addTime(Duration.ofSeconds(1));
        LOG.info("Tick second to {}", clockMock.instant());
        this.updateEverySecondRunnable.run();
    }

    public void tickMinute()
    {
        tickMinute(Duration.ofMinutes(1));
    }

    public void tickDay()
    {
        tickDay(LocalTime.of(8, 0));
    }

    public void tickDay(LocalTime time)
    {
        final LocalDateTime now = LocalDateTime.now(clockMock);
        final LocalDateTime tomorrow = LocalDateTime.of(now.toLocalDate().plusDays(1), time);
        final Duration duration = Duration.between(now, tomorrow);
        addTime(duration);
        LOG.info("Tick day by {} to {}", duration, clockMock.instant());
        this.updateEverySecondRunnable.run();
        this.updateEveryMinuteRunnable.run();
        this.updateEveryDayRunnable.run();
    }

    public void tickMinute(Duration duration)
    {
        addTime(duration);
        LOG.info("Tick minute by {} to {}", duration, clockMock.instant());
        this.updateEverySecondRunnable.run();
        this.updateEveryMinuteRunnable.run();
    }

    private void addTime(final Duration duration)
    {
        assertThat(duration).isPositive();
        setCurrentTime(clockMock.instant().plus(duration));
    }

    private void setCurrentTime(final Instant now)
    {
        when(clockMock.instant()).thenReturn(now);
    }

    public void captureScheduledRunnables()
    {
        final ArgumentCaptor<Runnable> arg = ArgumentCaptor.forClass(Runnable.class);
        verify(this.executorServiceMock, times(3)).schedule(arg.capture(), eq(0L), eq(TimeUnit.MILLISECONDS));

        this.updateEverySecondRunnable = arg.getAllValues().get(0);
        this.updateEveryDayRunnable = arg.getAllValues().get(1);
        this.updateEveryMinuteRunnable = arg.getAllValues().get(2);

        LOG.trace("Found callback for seconds: {}", updateEverySecondRunnable);
        LOG.trace("Found callback for days: {}", updateEveryDayRunnable);
        LOG.trace("Found callback for minutes: {}", updateEveryMinuteRunnable);

        assertAll(
                () -> assertThat(updateEverySecondRunnable.toString())
                        .contains("trigger=PeriodicTrigger [roundToUnit=Seconds]"),
                () -> assertThat(updateEveryDayRunnable.toString())
                        .contains("trigger=PeriodicTrigger [roundToUnit=Days]"),
                () -> assertThat(updateEveryMinuteRunnable.toString())
                        .contains("trigger=PeriodicTrigger [roundToUnit=Minutes]"));
    }

    public Clock clock()
    {
        return clockMock;
    }

    public ScheduledExecutorService executorService()
    {
        return executorServiceMock;
    }
}
