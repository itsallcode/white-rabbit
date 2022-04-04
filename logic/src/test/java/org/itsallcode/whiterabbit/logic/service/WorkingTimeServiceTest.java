package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.AppServiceCallback.InterruptionDetectedDecision;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.itsallcode.whiterabbit.logic.storage.data.JsonDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkingTimeServiceTest
{
    private static final LocalDate TODAY = LocalDate.of(2021, 11, 8);
    private static final ZoneId TIMEZONE = ZoneId.of("UTC");

    @Mock
    private Storage storageMock;
    @Mock
    private ClockService clockServiceMock;
    @Mock
    private AppServiceCallback appServiceCallbackMock;
    @Mock
    private MonthIndex monthMock;
    @Mock
    private ProjectService projectServiceMock;
    private WorkingTimeService workingTimeService;

    private DayRecord day;

    @BeforeEach
    void setUp()
    {
        workingTimeService = new WorkingTimeService(storageMock, clockServiceMock, appServiceCallbackMock);
        lenient().when(clockServiceMock.getCurrentDate()).thenReturn(TODAY);
        lenient().when(storageMock.loadOrCreate(YearMonth.from(TODAY))).thenReturn(monthMock);
        final JsonDay jsonDay = new JsonDay();
        jsonDay.setDate(TODAY);
        day = new DayRecord(null, jsonDay, null, monthMock, projectServiceMock, null);
        lenient().when(monthMock.getDay(TODAY)).thenReturn(day);
    }

    @Test
    void updateFreshDay()
    {
        when(clockServiceMock.getCurrentTime()).thenReturn(LocalTime.of(8, 0));

        workingTimeService.updateNow();

        assertThat(day.getBegin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getEnd()).isEqualTo(LocalTime.of(8, 0));
        verify(storageMock).storeMonth(monthMock);
    }

    @Test
    void updateExistingDay()
    {
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(8, 0));

        when(clockServiceMock.getCurrentTime()).thenReturn(LocalTime.of(8, 1));

        workingTimeService.updateNow();

        assertThat(day.getBegin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getEnd()).isEqualTo(LocalTime.of(8, 1));
        verify(storageMock).storeMonth(monthMock);
    }

    @Test
    void updateExistingDayEndDateInFuture()
    {
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(9, 0));

        when(clockServiceMock.getCurrentTime()).thenReturn(LocalTime.of(8, 2));

        workingTimeService.updateNow();

        assertThat(day.getBegin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getEnd()).isEqualTo(LocalTime.of(9, 0));
        verify(storageMock, never()).storeMonth(monthMock);
    }

    @Test
    void updateExistingDayInterruptionActive()
    {
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(8, 0));

        final LocalTime now = LocalTime.of(8, 10);
        when(clockServiceMock.getCurrentTime()).thenReturn(now);
        when(clockServiceMock.instant()).thenReturn(Instant.from(ZonedDateTime.of(TODAY, now, TIMEZONE)));
        workingTimeService.startInterruption();

        workingTimeService.updateNow();

        assertThat(day.getBegin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getEnd()).isEqualTo(now);
        verify(storageMock).storeMonth(monthMock);
    }

    @Test
    void updateExistingDayOneMinuteSkipped()
    {
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(8, 0));

        when(clockServiceMock.getCurrentTime()).thenReturn(LocalTime.of(8, 2));

        workingTimeService.updateNow();

        assertThat(day.getBegin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getEnd()).isEqualTo(LocalTime.of(8, 2));
        verify(storageMock).storeMonth(monthMock);
    }

    @Test
    void updateExistingDayTwoMinutesSkipped()
    {
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(8, 0));

        when(clockServiceMock.getCurrentTime()).thenReturn(LocalTime.of(8, 3));
        when(appServiceCallbackMock.automaticInterruptionDetected(LocalTime.of(8, 0), Duration.ofMinutes(3)))
                .thenReturn(InterruptionDetectedDecision.SKIP_INTERRUPTION);

        workingTimeService.updateNow();

        assertThat(day.getBegin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getEnd()).isEqualTo(LocalTime.of(8, 3));
        assertThat(day.getInterruption()).isZero();
        verify(storageMock).storeMonth(monthMock);
    }

    @Test
    void updateExistingDayThreeMinutesSkipped()
    {
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(8, 0));

        when(clockServiceMock.getCurrentTime()).thenReturn(LocalTime.of(8, 4));
        when(appServiceCallbackMock.automaticInterruptionDetected(LocalTime.of(8, 0), Duration.ofMinutes(4)))
                .thenReturn(InterruptionDetectedDecision.SKIP_INTERRUPTION);

        workingTimeService.updateNow();

        assertThat(day.getBegin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getEnd()).isEqualTo(LocalTime.of(8, 4));
        assertThat(day.getInterruption()).isZero();
        verify(storageMock).storeMonth(monthMock);
    }

    @Test
    void updateAutomaticInterruptionAdd()
    {
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(8, 0));

        when(clockServiceMock.getCurrentTime()).thenReturn(LocalTime.of(8, 4));
        when(appServiceCallbackMock.automaticInterruptionDetected(LocalTime.of(8, 0), Duration.ofMinutes(4)))
                .thenReturn(InterruptionDetectedDecision.ADD_INTERRUPTION);

        workingTimeService.updateNow();

        assertThat(day.getBegin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getEnd()).isEqualTo(LocalTime.of(8, 4));
        assertThat(day.getInterruption()).isEqualTo(Duration.ofMinutes(4));
        verify(storageMock).storeMonth(monthMock);
    }

    @Test
    void updateAutomaticInterruptionStopWorking()
    {
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(8, 0));

        when(clockServiceMock.getCurrentTime()).thenReturn(LocalTime.of(8, 4));
        when(appServiceCallbackMock.automaticInterruptionDetected(LocalTime.of(8, 0), Duration.ofMinutes(4)))
                .thenReturn(InterruptionDetectedDecision.STOP_WORKING_FOR_TODAY);

        workingTimeService.updateNow();

        assertThat(day.getBegin()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getEnd()).isEqualTo(LocalTime.of(8, 0));
        assertThat(day.getInterruption()).isZero();
        verify(storageMock, never()).storeMonth(monthMock);
    }

    @Test
    void startInterruption()
    {
        when(clockServiceMock.instant())
                .thenReturn(Instant.from(ZonedDateTime.of(TODAY, LocalTime.of(8, 0), TIMEZONE)));
        final Interruption interruption = workingTimeService.startInterruption();
        assertThat(interruption).isNotNull();
    }

    @Test
    void startInterruptionTwiceFails()
    {
        when(clockServiceMock.instant())
                .thenReturn(Instant.from(ZonedDateTime.of(TODAY, LocalTime.of(8, 0), TIMEZONE)));

        final Interruption interruption = workingTimeService.startInterruption();
        assertThat(interruption).isNotNull();

        when(clockServiceMock.instant())
                .thenReturn(Instant.from(ZonedDateTime.of(TODAY, LocalTime.of(8, 10), TIMEZONE)));

        assertThatThrownBy(() -> workingTimeService.startInterruption()).isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "An interruption was already started: Interruption [start=2021-11-08T08:00:00Z, currently: PT10M, duration=null]");
    }

    @Test
    void addingInterruptionUpdatesEndTime()
    {
        when(clockServiceMock.getCurrentTime()).thenReturn(LocalTime.of(10, 30));
        workingTimeService.addInterruption(TODAY, Duration.ofMinutes(5));

        assertAll(() -> assertThat(day.getEnd()).isEqualTo(LocalTime.of(10, 30)),
                () -> assertThat(day.getInterruption()).isEqualTo(Duration.ofMinutes(5)));
    }
}
