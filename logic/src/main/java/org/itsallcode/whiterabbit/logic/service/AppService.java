package org.itsallcode.whiterabbit.logic.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.Closeable;
import java.time.Duration;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.scheduling.PeriodicTrigger;
import org.itsallcode.whiterabbit.logic.service.scheduling.ScheduledTaskFuture;
import org.itsallcode.whiterabbit.logic.service.scheduling.Trigger;
import org.itsallcode.whiterabbit.logic.service.singleinstance.OtherInstance;
import org.itsallcode.whiterabbit.logic.service.singleinstance.RegistrationResult;
import org.itsallcode.whiterabbit.logic.service.singleinstance.RunningInstanceCallback;
import org.itsallcode.whiterabbit.logic.service.singleinstance.SingleInstanceService;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReport;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReportGenerator;
import org.itsallcode.whiterabbit.logic.storage.DateToFileMapper;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class AppService implements Closeable
{
    private static final Logger LOG = LogManager.getLogger(AppService.class);

    private final WorkingTimeService workingTimeService;
    private final Storage storage;
    private final ClockService clock;
    private final FormatterService formatterService;
    private final SchedulingService schedulingService;
    private final DelegatingAppServiceCallback appServiceCallback;
    private final SingleInstanceService singleInstanceService;
    private final VacationReportGenerator vacationService;

    private RegistrationResult singleInstanceRegistration;

    public AppService(WorkingTimeService workingTimeService, Storage storage, FormatterService formatterService,
            ClockService clock, SchedulingService schedulingService, SingleInstanceService singleInstanceService,
            DelegatingAppServiceCallback appServiceCallback, VacationReportGenerator vacationService)
    {
        this.workingTimeService = workingTimeService;
        this.storage = storage;
        this.formatterService = formatterService;
        this.clock = clock;
        this.schedulingService = schedulingService;
        this.singleInstanceService = singleInstanceService;
        this.appServiceCallback = appServiceCallback;
        this.vacationService = vacationService;
    }

    public static AppService create(final Config config, final FormatterService formatterService)
    {
        final SingleInstanceService singleInstanceService = new SingleInstanceService();
        final Storage storage = new Storage(new DateToFileMapper(config.getDataDir()));
        final ClockService clockService = new ClockService();
        final SchedulingService schedulingService = new SchedulingService(clockService);
        final DelegatingAppServiceCallback appServiceCallback = new DelegatingAppServiceCallback();
        final WorkingTimeService workingTimeService = new WorkingTimeService(storage, clockService, appServiceCallback);
        final VacationReportGenerator vacationService = new VacationReportGenerator(storage);
        return new AppService(workingTimeService, storage, formatterService, clockService, schedulingService,
                singleInstanceService, appServiceCallback, vacationService);
    }

    public void setUpdateListener(AppServiceCallback callback)
    {
        this.appServiceCallback.setDelegate(callback);
    }

    public Optional<OtherInstance> registerSingleInstance(RunningInstanceCallback callback)
    {
        singleInstanceRegistration = singleInstanceService.tryToRegisterInstance(callback);
        if (singleInstanceRegistration.isOtherInstanceRunning())
        {
            return Optional.of(singleInstanceRegistration);
        }
        else
        {
            return Optional.empty();
        }
    }

    private void assertSingleInstance()
    {
        if (singleInstanceRegistration == null)
        {
            throw new IllegalStateException(
                    "Single instance not registered. Call registerSingleInstance() before starting.");
        }
        if (singleInstanceRegistration.isOtherInstanceRunning())
        {
            throw new IllegalStateException("Another instance is running");
        }
    }

    public void start()
    {
        assertSingleInstance();
        schedule(PeriodicTrigger.everyMinute(), workingTimeService::updateNow);
    }

    public ScheduledTaskFuture schedule(Trigger trigger, Runnable runnable)
    {
        return this.schedulingService.schedule(trigger, runnable);
    }

    public void report()
    {
        final DayReporter reporter = new DayReporter(formatterService);
        storage.loadAll().getDays().forEach(reporter::add);
        reporter.finish();
    }

    public void updatePreviousMonthOvertimeField()
    {
        final List<MonthIndex> months = storage.loadAll().getMonths().stream()
                .sorted(Comparator.comparing(MonthIndex::getYearMonth)) //
                .collect(toList());
        Duration totalOvertime = Duration.ZERO;
        for (final MonthIndex month : months)
        {
            LOG.info("Updating overtime {} for month {}", totalOvertime, month.getYearMonth());
            month.setOvertimePreviousMonth(totalOvertime);
            totalOvertime = month.getTotalOvertime().truncatedTo(ChronoUnit.MINUTES);
            storage.storeMonth(month);
        }
    }

    public ClockService getClock()
    {
        return clock;
    }

    public List<DayRecord> getRecords(YearMonth yearMonth)
    {
        return getMonth(yearMonth) //
                .map(record -> record.getSortedDays().collect(toList())) //
                .orElse(emptyList());
    }

    public List<YearMonth> getAvailableDataYearMonth()
    {
        return storage.getAvailableDataYearMonth();
    }

    public Optional<MonthIndex> getMonth(YearMonth yearMonth)
    {
        return storage.loadMonth(yearMonth);
    }

    public MonthIndex getOrCreateMonth(YearMonth yearMonth)
    {
        return storage.loadOrCreate(yearMonth);
    }

    public void store(DayRecord record)
    {
        final MonthIndex monthRecord = storage.loadOrCreate(YearMonth.from(record.getDate()));
        monthRecord.put(record);
        storage.storeMonth(monthRecord);
        appServiceCallback.recordUpdated(record);
    }

    public Interruption startInterruption()
    {
        return workingTimeService.startInterruption();
    }

    public void toggleStopWorkForToday()
    {
        workingTimeService.toggleStopWorkForToday();
    }

    public void updateNow()
    {
        LOG.debug("Update now");
        workingTimeService.updateNow();
    }

    public VacationReport getVacationReport()
    {
        return vacationService.generateReport();
    }

    @Override
    public void close()
    {
        LOG.debug("Shutting down...");
        if (singleInstanceRegistration != null)
        {
            singleInstanceRegistration.close();
        }
        schedulingService.close();
    }
}
