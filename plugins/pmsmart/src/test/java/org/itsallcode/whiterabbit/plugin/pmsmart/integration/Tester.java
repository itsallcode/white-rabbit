package org.itsallcode.whiterabbit.plugin.pmsmart.integration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportGenerator;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportImpl;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportImpl.DayImpl;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportImpl.ProjectActivityImpl;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.holidays.HolidayService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.storage.CachingStorage;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.itsallcode.whiterabbit.logic.storage.data.JsonFileStorage;
import org.itsallcode.whiterabbit.logic.storage.data.JsonModelFactory;
import org.itsallcode.whiterabbit.logic.storage.data.JsonbFactory;
import org.itsallcode.whiterabbit.plugin.pmsmart.PMSmartExporter;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverFactory;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.DateSelector;

/**
 * Intended for manual tests.
 * 
 * <p>
 * Additionally in class {@link DateSelector} you could insert
 * </p>
 * 
 * <pre>
 * private static final Logger LOG = LogManager.getLogger(DateSelector.class);
 * </pre>
 * 
 * and in method {@link DateSelector#select()} you could insert
 * 
 * <pre>
 * if (day.isEqual(LocalDate.of(1970, 12, 31)))
 * {
 *     LOG.debug("Selecting critical date");
 * }
 * </pre>
 */
public class Tester
{
    private static final String PMSMART_URL = "http://pmsmart";
    private static final String PROJECT_FILE = "/path/to/projects.json";
    private static final String TIME_RECORDING_FILE = "/path/to/time-recordings/year/year-month.json";
    private static final long HOURS_PER_DAY = 8L;

    public static void main(String[] args)
    {
        final PluginConfiguration configMock = new TestingPluginConfiguration();
        final PMSmartExporter exporter = new PMSmartExporter(configMock, new WebDriverFactory());
        final ProjectReport report = createFullProjectReport();
        exporter.export(report, new NullProgressMonitor());
    }

    @SuppressWarnings("unused")
    private static ProjectReport createShortProjectReport()
    {
        final String comment = "aslködfj 123";
        final ProjectReportActivity activity = new ProjectActivityImpl(new ProjectImpl("P1000", "p1000", "P1000"),
                Duration.ofHours(1), comment);
        final ProjectReportDay day1 = new DayImpl(LocalDate.of(2021, 5, 17),
                DayType.WORK, "comment",
                List.of(activity));
        return new ProjectReportImpl(YearMonth.of(2021, 5), List.of(day1));
    }

    private static ProjectReport createFullProjectReport()
    {
        final JsonFileStorage dataStorage = new JsonFileStorageMock(Paths.get(TIME_RECORDING_FILE));
        final ProjectService projectService = ProjectService.load(Paths.get(PROJECT_FILE));
        final ContractTermsService contractTerms = new ContractTermsService(
                Optional.of(Duration.ofHours(HOURS_PER_DAY)));
        final HolidayService holidayService = new HolidayService(new ArrayList<>());

        final Storage storage = CachingStorage.create(dataStorage, contractTerms, projectService, holidayService);
        final ProjectReportGenerator generator = new ProjectReportGenerator(storage);
        return generator.generateReport(null);
    }

    private static final class TestingPluginConfiguration implements PluginConfiguration
    {
        @Override
        public String getMandatoryValue(String propertyName)
        {
            return PMSMART_URL;
        }

        @Override
        public Optional<String> getOptionalValue(String propertyName)
        {
            return Optional.empty();
        }

        @Override
        public Path getDataDir()
        {
            return null;
        }
    }

    private static final class NullProgressMonitor implements ProgressMonitor
    {
        private static final Logger LOG = LogManager.getLogger(NullProgressMonitor.class);

        @Override
        public void worked(int work)
        {
        }

        @Override
        public void setTaskName(String name)
        {
            LOG.debug(name);
        }

        @Override
        public boolean isCanceled()
        {
            return false;
        }

        @Override
        public void beginTask(String name, int totalWork)
        {
        }
    }

    private static final class JsonFileStorageMock extends JsonFileStorage
    {
        private final Path timeRecordingFile;

        private JsonFileStorageMock(Path timeRecordingFile)
        {
            super(new JsonbFactory().create(), null, new JsonModelFactory());
            this.timeRecordingFile = timeRecordingFile;
        }

        @Override
        public Optional<MonthData> load(YearMonth date)
        {
            return Optional.of(super.loadFromFile(timeRecordingFile));
        }
    }
}
