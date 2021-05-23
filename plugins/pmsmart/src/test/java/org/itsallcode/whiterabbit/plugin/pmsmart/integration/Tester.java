package org.itsallcode.whiterabbit.plugin.pmsmart.integration;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportGenerator;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportImpl;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportImpl.DayImpl;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportImpl.ProjectActivityImpl;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.storage.CachingStorage;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.itsallcode.whiterabbit.logic.storage.data.JsonFileStorage;
import org.itsallcode.whiterabbit.plugin.pmsmart.PMSmartExporter;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverFactory;

/**
 * Intended for manual tests
 */
public class Tester
{
    private static final String PROJECT_FILE = "c:/HOME/Tmp/White-Rabbit-Test-Data/projects.json";
    private static final String TIME_RECORDING_FILE = "c:/HOME/Tmp/White-Rabbit-Test-Data/time-record.json";

    public static void main(String[] args)
    {
        final PluginConfiguration configMock = new TestingConfig();
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
        final Config config = new ConfigMock(Paths.get(PROJECT_FILE));
        final JsonFileStorage dataStorage = new JsonFileStorageMock(Paths.get(TIME_RECORDING_FILE));
        final ProjectService projectService = new ProjectService(config);
        final Storage storage = CachingStorage.create(dataStorage, new ContractTermsService(config),
                projectService);
        final ProjectReportGenerator generator = new ProjectReportGenerator(storage);
        return generator.generateReport(null);
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

    private static final class TestingConfig implements PluginConfiguration
    {
        @Override
        public String getMandatoryValue(String propertyName)
        {
            return "http://pmsmart";
        }
    }
}
