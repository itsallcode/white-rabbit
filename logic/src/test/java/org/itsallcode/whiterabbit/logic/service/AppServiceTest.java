package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.service.singleinstance.SingleInstanceService;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReportGenerator;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppServiceTest
{
    private static final Instant NOW = Instant.parse("2007-12-03T10:15:30.00Z");

    @Mock
    private Storage storageMock;
    @Mock
    private FormatterService formatterServiceMock;
    @Mock
    private ClockService clockMock;
    @Mock
    private SchedulingService schedulingServiceMock;
    @Mock
    private MonthIndex monthIndexMock;
    @Mock
    private Config configMock;
    @Mock
    private AppServiceCallback updateListenerMock;
    @Mock
    private SingleInstanceService singleInstanceService;
    @Mock
    private VacationReportGenerator vacationServiceMock;

    private AppService appService;

    @BeforeEach
    void setUp()
    {
        final DelegatingAppServiceCallback appServiceCallback = new DelegatingAppServiceCallback();
        appService = new AppService(new WorkingTimeService(storageMock, clockMock, appServiceCallback), storageMock,
                formatterServiceMock, clockMock, schedulingServiceMock, singleInstanceService, appServiceCallback,
                vacationServiceMock);
        appService.setUpdateListener(updateListenerMock);
    }

    @Test
    void testNoUpdateWhenNotWorkingDay()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 9);
        final JsonDay day = new JsonDay();
        day.setDate(today);

        updateNow(now, day);

        assertThat(day.getBegin()).isNull();
        assertThat(day.getEnd()).isNull();
    }

    @Test
    void testUpdateListenerNotCalledWhenNoUpdate()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 9);
        final JsonDay day = new JsonDay();
        day.setDate(today);

        updateNow(now, day);

        verifyNoInteractions(updateListenerMock);
    }

    @Test
    void testUpdateListenerCalledWhenDayRecordUpdated()
    {
        final LocalTime now = LocalTime.of(8, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);

        final DayRecord updatedRecord = updateNow(now, day);

        verify(updateListenerMock).recordUpdated(same(updatedRecord));
    }

    @Test
    void testUpdateNewDayOnWorkingDay()
    {
        final LocalTime now = LocalTime.of(8, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);

        updateNow(now, day);

        assertThat(day.getBegin()).isEqualTo(now);
        assertThat(day.getEnd()).isEqualTo(now);
    }

    @Test
    void testUpdateExistingDayAfter1Min()
    {
        final LocalTime begin = LocalTime.of(8, 0);
        final LocalTime now = LocalTime.of(8, 1);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(begin);
        day.setEnd(begin);

        updateNow(now, day);

        assertThat(day.getBegin()).isEqualTo(begin);
        assertThat(day.getEnd()).isEqualTo(now);
    }

    @Test
    void testUpdateExistingDayAfter2Min()
    {
        final LocalTime begin = LocalTime.of(8, 0);
        final LocalTime now = LocalTime.of(8, 2);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(begin);
        day.setEnd(begin);

        updateNow(now, day);

        assertThat(day.getBegin()).isEqualTo(begin);
        assertThat(day.getEnd()).isEqualTo(now);
    }

    @Test
    void testNoUpdateIfEndAfterNow()
    {
        final LocalTime begin = LocalTime.of(8, 0);
        final LocalTime end = LocalTime.of(16, 0);
        final LocalTime now = LocalTime.of(9, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(begin);
        day.setEnd(end);

        updateNow(now, day);

        assertThat(day.getBegin()).isEqualTo(begin);
        assertThat(day.getEnd()).isEqualTo(end);
    }

    @Test
    void testUpdateExistingDayAfter3Min()
    {
        final LocalTime begin = LocalTime.of(8, 0);
        final LocalTime now = LocalTime.of(8, 3);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(begin);
        day.setEnd(begin);

        updateNow(now, day);

        assertThat(day.getBegin()).isEqualTo(begin);
        assertThat(day.getEnd()).isEqualTo(now);
    }

    @Test
    void testUpdateNowAddsInterruption()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(13, 0));

        when(updateListenerMock.shouldAddAutomaticInterruption(day.getEnd(), Duration.ofHours(1))).thenReturn(true);

        updateNow(now, day);

        assertThat(day.getInterruption()).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void testUpdateNowDoesNotAddInterruptionWhenCallbackSaysNo()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(13, 0));

        when(updateListenerMock.shouldAddAutomaticInterruption(day.getEnd(), Duration.ofHours(1))).thenReturn(false);

        updateNow(now, day);

        assertThat(day.getInterruption()).isEqualTo(null);
    }

    @Test
    void testUpdateDoesNotUpdateBeginIfInThePast()
    {
        final LocalTime now = LocalTime.of(9, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        final LocalTime begin = LocalTime.of(8, 0);
        day.setBegin(begin);
        day.setEnd(null);

        updateNow(now, day);

        assertThat(day.getBegin()).isEqualTo(begin);
        assertThat(day.getEnd()).isEqualTo(now);
    }

    @Test
    void testUpdateDoesUpdateBeginIfInTheFuture()
    {
        final LocalTime now = LocalTime.of(9, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        final LocalTime begin = LocalTime.of(10, 0);
        day.setBegin(begin);
        day.setEnd(null);

        updateNow(now, day);

        assertThat(day.getBegin()).isEqualTo(now);
        assertThat(day.getEnd()).isEqualTo(now);
    }

    @Test
    void testUpdateNowDoesNotAddInterruptionIfAlreadyRunning()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(13, 0));

        appService.startInterruption();

        updateNow(now, day);

        assertThat(day.getInterruption()).isNull();
    }

    @Test
    void testStartingInterruptionTwiceThrowsException()
    {
        when(clockMock.instant()).thenReturn(NOW);
        appService.startInterruption();

        assertThrows(IllegalStateException.class, () -> appService.startInterruption());
    }

    @Test
    void testGetClock()
    {
        assertThat(appService.getClock()).isSameAs(clockMock);
    }

    @Test
    void testShutdown()
    {
        appService.close();
        verify(schedulingServiceMock).close();
    }

    @Test
    void testStartAutoUpdate()
    {
        appService.start();
        verify(schedulingServiceMock).schedule(any(), ArgumentMatchers.any(Runnable.class));
    }

    private DayRecord updateNow(final LocalTime now, final JsonDay day)
    {
        when(clockMock.getCurrentDate()).thenReturn(day.getDate());
        when(clockMock.getCurrentTime()).thenReturn(now);
        when(storageMock.loadOrCreate(YearMonth.from(day.getDate()))).thenReturn(monthIndexMock);
        final DayRecord dayRecord = new DayRecord(day, null, monthIndexMock);
        when(monthIndexMock.getDay(day.getDate())).thenReturn(dayRecord);

        appService.updateNow();
        return dayRecord;
    }
}
