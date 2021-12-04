package org.itsallcode.whiterabbit.plugin.pmsmart;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportImpl;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverFactory;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.ProjectRow;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.ProjectTable;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.WeekViewPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PMSmartExporterTest
{
    private static final String COST_CARRIER = "costcarrier";
    private static final String BASE_URL = "base url";
    @Mock
    PluginConfiguration configMock;
    @Mock
    WebDriverFactory webDriverFactoryMock;
    @Mock
    ProgressMonitor progressMonitorMock;
    @Mock
    private Driver driverMock;
    @Mock
    private WeekViewPage weekViewPageMock;
    @Mock
    private ProjectTable projectTableMock;
    @Mock
    private ProjectRow projectRowMock;
    @Mock
    private ProjectRow otherProjectRowMock;

    PMSmartExporter exporter;

    @BeforeEach
    void setUp()
    {
        exporter = new PMSmartExporter(configMock, webDriverFactoryMock);
        when(configMock.getMandatoryValue("baseurl")).thenReturn(BASE_URL);
        when(webDriverFactoryMock.createWebDriver(BASE_URL)).thenReturn(driverMock);
        when(driverMock.getWeekViewPage()).thenReturn(weekViewPageMock);
        when(weekViewPageMock.getProjectTable()).thenReturn(projectTableMock);
        when(projectTableMock.getProjects())
                .thenReturn(Map.of(COST_CARRIER, projectRowMock, "other cost carrier", otherProjectRowMock));
    }

    @Test
    void exportActivity()
    {
        final LocalDate date = LocalDate.of(2021, Month.MAY, 3);
        runExport(day(date, DayType.WORK, activity(COST_CARRIER, Duration.ofHours(1), "project1")));
        verify(projectRowMock).enterComment(date, "project1");
        verify(projectRowMock).enterDuration(date, Duration.ofHours(1));
        verify(otherProjectRowMock, never()).enterDuration(date, Duration.ZERO);
    }

    @Test
    void withoutComments()
    {
        when(configMock.getOptionalValue(PMSmartExporter.TRANSFER_COMMENTS)).thenReturn(Optional.of("false"));
        final LocalDate date = LocalDate.of(2021, Month.MAY, 3);
        runExport(day(date, DayType.WORK, activity(COST_CARRIER, Duration.ofHours(1), "project1")));
        verify(projectRowMock).enterDuration(date, Duration.ofHours(1));
        verify(projectRowMock, never()).enterComment(any(), any());
    }

    @Test
    void clearOtherProjects()
    {
        when(configMock.getOptionalValue(PMSmartExporter.TRANSFER_COMMENTS)).thenReturn(Optional.of("true"));
        when(configMock.getOptionalValue(PMSmartExporter.CLEAR_OTHER_PROJECTS)).thenReturn(Optional.of("true"));
        final LocalDate date = LocalDate.of(2021, Month.MAY, 3);
        runExport(day(date, DayType.WORK, activity(COST_CARRIER, Duration.ofHours(1), "project1")));
        verify(projectRowMock).enterDuration(date, Duration.ofHours(1));
        verify(otherProjectRowMock).enterDuration(date, Duration.ZERO);
        verify(projectRowMock).enterComment(date, "project1");
    }

    @Test
    void exportOnlyWorkingDays()
    {
        final LocalDate workingDate = LocalDate.of(2021, Month.MAY, 3);
        final LocalDate sickDate = LocalDate.of(2021, Month.MAY, 4);
        runExport(day(workingDate, DayType.WORK, activity(COST_CARRIER, Duration.ofHours(1), "project1")),
                day(sickDate, DayType.SICK, activity(COST_CARRIER, Duration.ofHours(1), "project2")));
        verify(weekViewPageMock).isDaySelected(workingDate);
        verify(weekViewPageMock, never()).isDaySelected(sickDate);
    }

    @Test
    void exportOnlyDaysWithActivities()
    {
        final LocalDate dateWithActivities = LocalDate.of(2021, Month.MAY, 3);
        final LocalDate dateWithoutActivities = LocalDate.of(2021, Month.MAY, 4);
        runExport(day(dateWithActivities, DayType.WORK, activity(COST_CARRIER, Duration.ofHours(1), "project1")),
                day(dateWithoutActivities, DayType.WORK));
        verify(weekViewPageMock).isDaySelected(dateWithActivities);
        verify(weekViewPageMock, never()).isDaySelected(dateWithoutActivities);
    }

    private void runExport(ProjectReportDay... days)
    {
        exporter.export(projectReport(days), progressMonitorMock);
    }

    private ProjectReportImpl projectReport(ProjectReportDay... days)
    {
        return new ProjectReportImpl(YearMonth.of(2021, Month.MAY), asList(days));
    }

    private ProjectReportDay day(LocalDate date, DayType type, ProjectReportActivity... projects)
    {
        return new ProjectReportImpl.DayImpl(date, type, "day comment", asList(projects));
    }

    private ProjectReportActivity activity(String costCarrier, Duration workingTime, String comment)
    {
        return new ProjectReportImpl.ProjectActivityImpl(new ProjectImpl(null, null, costCarrier), workingTime,
                List.of(comment));
    }
}
