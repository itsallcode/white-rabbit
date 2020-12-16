package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.autocomplete.AutocompleteService;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReportGenerator;
import org.itsallcode.whiterabbit.logic.service.AppPropertiesService.AppProperties;
import org.itsallcode.whiterabbit.logic.service.AppServiceCallback.InterruptionDetectedDecision;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.service.scheduling.SchedulingService;
import org.itsallcode.whiterabbit.logic.service.singleinstance.RegistrationResult;
import org.itsallcode.whiterabbit.logic.service.singleinstance.RunningInstanceCallback;
import org.itsallcode.whiterabbit.logic.service.singleinstance.SingleInstanceService;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.itsallcode.whiterabbit.logic.test.TestingConfig;
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
    @Mock
    private ActivityService activityService;
    @Mock
    private ProjectService projectServiceMock;
    @Mock
    private AppPropertiesService appPropertiesServiceMock;
    @Mock
    private AutocompleteService autocompleteServiceMock;

    private AppService appService;

    @BeforeEach
    void setUp()
    {
        final DelegatingAppServiceCallback appServiceCallback = new DelegatingAppServiceCallback();
        final WorkingTimeService workingTimeService = new WorkingTimeService(storageMock, clockMock,
                appServiceCallback);
        appService = new AppService(workingTimeService, storageMock, formatterServiceMock, clockMock,
                schedulingServiceMock, singleInstanceService, appServiceCallback,
                vacationServiceMock, activityService, projectServiceMock, autocompleteServiceMock,
                appPropertiesServiceMock);
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

        when(updateListenerMock.automaticInterruptionDetected(day.getEnd(), Duration.ofMinutes(2)))
                .thenReturn(InterruptionDetectedDecision.SKIP_INTERRUPTION);

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

        when(updateListenerMock.automaticInterruptionDetected(day.getEnd(), Duration.ofMinutes(3)))
                .thenReturn(InterruptionDetectedDecision.SKIP_INTERRUPTION);

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

        when(updateListenerMock.automaticInterruptionDetected(day.getEnd(), Duration.ofHours(1)))
                .thenReturn(InterruptionDetectedDecision.ADD_INTERRUPTION);

        updateNow(now, day);

        assertThat(day.getEnd()).isEqualTo(now);
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

        when(updateListenerMock.automaticInterruptionDetected(day.getEnd(), Duration.ofHours(1)))
                .thenReturn(InterruptionDetectedDecision.SKIP_INTERRUPTION);

        updateNow(now, day);

        assertThat(day.getEnd()).isEqualTo(now);
        assertThat(day.getInterruption()).isNull();
    }

    @Test
    void testUpdateNowDoesNotAddInterruptionWhenCallbackStopWorkForToday()
    {
        final LocalTime beforeInterruption = LocalTime.of(13, 0);
        final LocalTime now = beforeInterruption.plusHours(1);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(beforeInterruption);

        when(updateListenerMock.automaticInterruptionDetected(day.getEnd(), Duration.ofHours(1)))
                .thenReturn(InterruptionDetectedDecision.STOP_WORKING_FOR_TODAY);

        updateNow(now, day);

        assertThat(day.getEnd()).isEqualTo(beforeInterruption);
        assertThat(day.getInterruption()).isNull();
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

        final Instant instant = LocalDateTime.of(today, now).toInstant(ZoneOffset.ofHours(0));
        when(clockMock.instant()).thenReturn(instant);

        appService.startInterruption();

        updateNow(now, day);

        assertThat(day.getInterruption()).isNull();
    }

    @Test
    void addInterruptionCallback()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(13, 0));

        final Instant instant = LocalDateTime.of(today, now).toInstant(ZoneOffset.ofHours(0));
        when(clockMock.instant()).thenReturn(instant);

        final Interruption interruption = appService.startInterruption();

        updateNow(now, day);

        assertThat(day.getInterruption()).isNull();

        when(clockMock.instant()).thenReturn(instant.plus(3, ChronoUnit.MINUTES));
        interruption.end();
        assertThat(day.getInterruption()).isEqualTo(Duration.ofMinutes(3));
    }

    @Test
    void cancelInterruptionCallback()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(LocalTime.of(13, 0));

        final Instant instant = LocalDateTime.of(today, now).toInstant(ZoneOffset.ofHours(0));
        when(clockMock.instant()).thenReturn(instant);

        final Interruption interruption = appService.startInterruption();

        updateNow(now, day);

        assertThat(day.getInterruption()).isNull();

        when(clockMock.instant()).thenReturn(instant.plus(3, ChronoUnit.MINUTES));
        interruption.cancel();
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
    void shutdownClosesSchedulingService()
    {
        appService.close();
        verify(schedulingServiceMock).close();
    }

    @Test
    void startFailsWhenInstanceNotRegistered()
    {
        assertThatThrownBy(() -> appService.start()).isInstanceOf(IllegalStateException.class)
                .hasMessage("Single instance not registered. Call registerSingleInstance() before starting.");
    }

    @Test
    void testStartAutoUpdate()
    {
        final RunningInstanceCallback callbackMock = mock(RunningInstanceCallback.class);
        final RegistrationResult registrationMock = mock(RegistrationResult.class);
        when(registrationMock.isOtherInstanceRunning()).thenReturn(false);
        when(singleInstanceService.tryToRegisterInstance(callbackMock)).thenReturn(registrationMock);

        appService.registerSingleInstance(callbackMock);
        appService.start();
        verify(schedulingServiceMock).schedule(any(), ArgumentMatchers.any(Runnable.class));
    }

    @Test
    void toggleStopWorkForTodayCurrentlyNotStopped()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(now);
        updateNow(now, day);

        appService.toggleStopWorkForToday();

        verify(updateListenerMock).workStoppedForToday(true);
    }

    @Test
    void toggleStopWorkForTodayAlreadyStopped()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(now);
        updateNow(now, day);

        appService.toggleStopWorkForToday();
        verify(updateListenerMock).workStoppedForToday(true);

        appService.toggleStopWorkForToday();
        verify(updateListenerMock).workStoppedForToday(false);
    }

    @Test
    void endTimeNotUpdatedWhenWorkStoppedForToday()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(now);
        updateNow(now, day);

        appService.toggleStopWorkForToday();

        assertThat(day.getEnd()).isEqualTo(now);

        lenient().when(clockMock.getCurrentTime()).thenReturn(now.plusMinutes(1));

        appService.updateNow();
        assertThat(day.getEnd()).isEqualTo(now);

        verify(clockMock, times(1)).getCurrentTime();
    }

    @Test
    void endTimeNotUpdatedWhenWorkNotStoppedForToday()
    {
        final LocalTime now = LocalTime.of(14, 0);
        final LocalDate today = LocalDate.of(2019, 3, 8);
        final JsonDay day = new JsonDay();
        day.setDate(today);
        day.setBegin(LocalTime.of(8, 0));
        day.setEnd(now);
        updateNow(now, day);

        appService.toggleStopWorkForToday();
        appService.toggleStopWorkForToday();

        assertThat(day.getEnd()).isEqualTo(now);

        when(clockMock.getCurrentTime()).thenReturn(now.plusMinutes(1));

        appService.updateNow();
        assertThat(day.getEnd()).isEqualTo(now.plusMinutes(1));

        verify(clockMock, times(3)).getCurrentTime();
    }

    @Test
    void loadAppProperties()
    {
        final AppProperties appPropertiesMock = mock(AppProperties.class);
        when(appPropertiesServiceMock.load()).thenReturn(appPropertiesMock);

        assertThat(appService.getAppProperties()).isSameAs(appPropertiesMock);
    }

    private DayRecord updateNow(final LocalTime now, final JsonDay day)
    {
        when(clockMock.getCurrentDate()).thenReturn(day.getDate());
        when(clockMock.getCurrentTime()).thenReturn(now);
        when(storageMock.loadOrCreate(YearMonth.from(day.getDate()))).thenReturn(monthIndexMock);
        final DayRecord dayRecord = new DayRecord(new ContractTermsService(TestingConfig.builder().build()), day, null,
                monthIndexMock, projectServiceMock);
        when(monthIndexMock.getDay(day.getDate())).thenReturn(dayRecord);

        appService.updateNow();
        return dayRecord;
    }
}
