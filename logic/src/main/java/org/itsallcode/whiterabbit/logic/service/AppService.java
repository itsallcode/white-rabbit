package org.itsallcode.whiterabbit.logic.service;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.scheduling.PeriodicTrigger;
import org.itsallcode.whiterabbit.logic.service.scheduling.ScheduledTaskFuture;
import org.itsallcode.whiterabbit.logic.service.scheduling.Trigger;
import org.itsallcode.whiterabbit.logic.storage.DateToFileMapper;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class AppService
{
    private static final Logger LOG = LogManager.getLogger(AppService.class);

    private final WorkingTimeService workingTimeService;
    private final Storage storage;
    private final ClockService clock;
    private final FormatterService formatterService;
    private final SchedulingService schedulingService;
    private final DelegatingAppServiceCallback appServiceCallback;
    private final SingleInstanceService singleInstanceService;

    public AppService(WorkingTimeService workingTimeService, Storage storage, FormatterService formatterService,
            ClockService clock, SchedulingService schedulingService, SingleInstanceService singleInstanceService,
            DelegatingAppServiceCallback appServiceCallback)
    {
        this.workingTimeService = workingTimeService;
        this.storage = storage;
        this.formatterService = formatterService;
        this.clock = clock;
        this.schedulingService = schedulingService;
        this.singleInstanceService = singleInstanceService;
        this.appServiceCallback = appServiceCallback;
    }

    public static AppService create(final Config config, final FormatterService formatterService)
    {
        final SingleInstanceService singleInstanceService = new SingleInstanceService();
        singleInstanceService.registerInstance();
        final Storage storage = new Storage(new DateToFileMapper(config.getDataDir()));
        final ClockService clockService = new ClockService();
        final SchedulingService schedulingService = new SchedulingService(clockService);
        final DelegatingAppServiceCallback appServiceCallback = new DelegatingAppServiceCallback();
        final WorkingTimeService workingTimeService = new WorkingTimeService(storage, clockService, appServiceCallback);
        return new AppService(workingTimeService, storage, formatterService, clockService, schedulingService,
                singleInstanceService, appServiceCallback);
    }

    public void setUpdateListener(AppServiceCallback callback)
    {
        this.appServiceCallback.setDelegate(callback);
    }

    public void start()
    {
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
            totalOvertime = month.getTotalOvertime();
            storage.storeMonth(month);
        }
    }

    public void shutdown()
    {
        LOG.debug("Shutting down...");
        singleInstanceService.shutdown();
        this.schedulingService.shutdown();
    }

    public ClockService getClock()
    {
        return clock;
    }

    public List<DayRecord> getRecords(YearMonth yearMonth)
    {
        return getMonth(yearMonth).getSortedDays().collect(toList());
    }

    public List<YearMonth> getAvailableDataYearMonth()
    {
        return storage.getAvailableDataYearMonth();
    }

    public MonthIndex getMonth(YearMonth yearMonth)
    {
        return storage.loadMonth(yearMonth);
    }

    public void store(DayRecord record)
    {
        final MonthIndex month = storage.loadMonth(YearMonth.from(record.getDate()));
        month.put(record);
        storage.storeMonth(month);
        appServiceCallback.recordUpdated(record);
    }

    public Interruption startInterruption()
    {
        return workingTimeService.startInterruption();
    }

    public DayRecord updateNow()
    {
        return workingTimeService.updateNow();
    }
}
