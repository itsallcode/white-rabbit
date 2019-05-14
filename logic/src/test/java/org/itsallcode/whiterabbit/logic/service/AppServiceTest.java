package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
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

    private AppService appService;

    @BeforeEach
    void setUp()
    {
        appService = new AppService(storageMock, formatterServiceMock, clockMock,
                schedulingServiceMock);
    }

    @Test
    void testCreate()
    {
        assertThat(AppService.create(configMock, formatterServiceMock)).isNotNull();
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

        updateNow(now, day);

        assertThat(day.getInterruption()).isEqualTo(Duration.ofHours(1));
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
        appService.shutdown();
        verify(schedulingServiceMock).shutdown();
    }

    @Test
    void testStartAutoUpdate()
    {
        appService.startAutoUpdate(day -> {});
        verify(schedulingServiceMock).schedule(ArgumentMatchers.any(DayUpdateExecutor.class));
    }

    private void updateNow(final LocalTime now, final JsonDay day)
    {
        when(clockMock.getCurrentDate()).thenReturn(day.getDate());
        when(clockMock.getCurrentTime()).thenReturn(now);
        when(storageMock.loadMonth(YearMonth.from(day.getDate()))).thenReturn(monthIndexMock);
        when(monthIndexMock.getDay(day.getDate())).thenReturn(new DayRecord(day));

        appService.updateNow();
    }
}
