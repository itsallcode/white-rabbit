package org.itsallcode.whiterabbit.logic.service;

import static java.util.stream.Collectors.toList;

import java.io.Closeable;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.autocomplete.AutocompleteService;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportGenerator;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReportGenerator;
import org.itsallcode.whiterabbit.logic.service.AppPropertiesService.AppProperties;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.holidays.HolidayService;
import org.itsallcode.whiterabbit.logic.service.plugin.PluginManager;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.service.scheduling.PeriodicTrigger;
import org.itsallcode.whiterabbit.logic.service.scheduling.SchedulingService;
import org.itsallcode.whiterabbit.logic.service.singleinstance.OtherInstance;
import org.itsallcode.whiterabbit.logic.service.singleinstance.RegistrationResult;
import org.itsallcode.whiterabbit.logic.service.singleinstance.RunningInstanceCallback;
import org.itsallcode.whiterabbit.logic.service.singleinstance.SingleInstanceService;
import org.itsallcode.whiterabbit.logic.storage.CachingStorage;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.itsallcode.whiterabbit.logic.storage.data.JsonFileStorage;

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
    private final VacationReportGenerator vacationReportGenerator;
    private final ProjectReportGenerator projectReportGenerator;
    private final ActivityService activityService;
    private final ProjectService projectService;
    private final AppPropertiesService appPropertiesService;

    private RegistrationResult singleInstanceRegistration;

    private final AutocompleteService autocompleteService;

    private final PluginManager pluginManager;

    @SuppressWarnings("java:S107") // Large number of parameters is ok here.
    AppService(WorkingTimeService workingTimeService, Storage storage, FormatterService formatterService,
            ClockService clock, SchedulingService schedulingService, SingleInstanceService singleInstanceService,
            DelegatingAppServiceCallback appServiceCallback, ActivityService activityService,
            ProjectService projectService, AutocompleteService autocompleteService,
            AppPropertiesService appPropertiesService, VacationReportGenerator vacationReportGenerator,
            ProjectReportGenerator projectReportGenerator, PluginManager pluginManager)
    {
        this.workingTimeService = workingTimeService;
        this.storage = storage;
        this.formatterService = formatterService;
        this.clock = clock;
        this.schedulingService = schedulingService;
        this.singleInstanceService = singleInstanceService;
        this.appServiceCallback = appServiceCallback;
        this.vacationReportGenerator = vacationReportGenerator;
        this.activityService = activityService;
        this.projectService = projectService;
        this.projectReportGenerator = projectReportGenerator;
        this.autocompleteService = autocompleteService;
        this.appPropertiesService = appPropertiesService;
        this.pluginManager = pluginManager;
    }

    public static AppService create(final Config config)
    {
        return create(config, Clock.systemDefaultZone(), new ScheduledThreadPoolExecutor(1));
    }

    public static AppService create(final Config config, Clock clock, ScheduledExecutorService scheduledExecutor)
    {
        final SingleInstanceService singleInstanceService = SingleInstanceService.create(config);
        final ProjectService projectService = new ProjectService(config.getProjectFile());
        final PluginManager pluginManager = PluginManager.create(config);

        final MonthDataStorage dataStorage = createMonthDataStorage(config, pluginManager);
        final List<Holidays> holidayProvider = pluginManager.getAllFeatures(Holidays.class);
        final HolidayService holidayService = new HolidayService(holidayProvider);
        final CachingStorage storage = CachingStorage.create(dataStorage, new ContractTermsService(config),
                projectService, holidayService);

        final ClockService clockService = new ClockService(clock);
        final AutocompleteService autocompleteService = new AutocompleteService(storage, clockService);
        final SchedulingService schedulingService = new SchedulingService(clockService, scheduledExecutor);
        final DelegatingAppServiceCallback appServiceCallback = new DelegatingAppServiceCallback();
        final WorkingTimeService workingTimeService = new WorkingTimeService(storage, clockService, appServiceCallback);
        final VacationReportGenerator vacationReportGenerator = new VacationReportGenerator(storage);
        final ProjectReportGenerator projectReportGenerator = new ProjectReportGenerator(storage);
        final ActivityService activityService = new ActivityService(storage, autocompleteService, appServiceCallback);
        final FormatterService formatterService = new FormatterService(config.getLocale(), clock.getZone());
        return new AppService(workingTimeService, storage, formatterService, clockService, schedulingService,
                singleInstanceService, appServiceCallback, activityService, projectService, autocompleteService,
                new AppPropertiesService(), vacationReportGenerator,
                projectReportGenerator, pluginManager);
    }

    private static MonthDataStorage createMonthDataStorage(final Config config, final PluginManager pluginManager)
    {
        final Optional<MonthDataStorage> dataStorage = pluginManager.getUniqueFeature(MonthDataStorage.class);
        if (dataStorage.isPresent())
        {
            LOG.info("Using storage plugin {}", dataStorage.get().getClass().getName());
            return dataStorage.get();
        }
        LOG.debug("No storage plugin found, using default json file storage");
        return JsonFileStorage.create(config.getDataDir());
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
        scheduler().schedule(PeriodicTrigger.everyMinute(), this::updateNow);
    }

    public SchedulingService scheduler()
    {
        return this.schedulingService;
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

    public List<YearMonth> getAvailableDataYearMonth()
    {
        return storage.getAvailableDataMonths();
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

    public void addInterruption(LocalDate date, Duration interruption)
    {
        workingTimeService.addInterruption(date, interruption);
    }

    public void toggleStopWorkForToday()
    {
        workingTimeService.toggleStopWorkForToday();
    }

    public void updateNow()
    {
        workingTimeService.updateNow();
    }

    public VacationReport getVacationReport()
    {
        return vacationReportGenerator.generateReport();
    }

    public ProjectReport generateProjectReport(YearMonth month)
    {
        return projectReportGenerator.generateReport(month);
    }

    public ActivityService activities()
    {
        return activityService;
    }

    public ProjectService projects()
    {
        return projectService;
    }

    public FormatterService formatter()
    {
        return formatterService;
    }

    public AppProperties getAppProperties()
    {
        return appPropertiesService.load();
    }

    public AutocompleteService autocomplete()
    {
        return autocompleteService;
    }

    public PluginManager pluginManager()
    {
        return pluginManager;
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
        pluginManager.close();
    }
}
