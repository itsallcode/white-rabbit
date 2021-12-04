package org.itsallcode.whiterabbit.plugin.csv;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReportImpl;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CSVExporterTest
{
    @Mock
    ProgressMonitor progressMonitorMock;

    @Mock
    OutStreamProvider outStreamProvider;

    ByteArrayOutputStream tmpOutStream;

    @BeforeEach
    void setUp() throws IOException
    {
        tmpOutStream = new ByteArrayOutputStream();
        when(outStreamProvider.getStream(Mockito.anyString())).thenReturn(tmpOutStream);
    }

    @Test
    void exportCSVFilterForWorkdays()
    {
        runExport(true, createDays());
        assertThat(tmpOutStream).hasToString(lines(
                "Date,Project,TimePerProject,TimePerDay,Comment",
                "2021-06-04,,,01:00,day comment",
                ",Project9FromOuterSpace,01:00,,abc"));
    }

    @Test
    void exportCSVNoFilter()
    {
        runExport(false, createDays());
        assertThat(tmpOutStream).hasToString(lines(
                "Date,Project,TimePerProject,TimePerDay,Comment",
                "2021-06-04,,,01:00,day comment",
                ",Project9FromOuterSpace,01:00,,abc",
                "2021-06-05,,,01:00,day comment",
                ",Project9FromOuterSpace,01:00,,abc"));
    }

    @Test
    void exportMultipleProjectsPerDay()
    {
        final LocalDate dateOne = LocalDate.of(2021, Month.JUNE, 4);
        runExport(false,
                day(dateOne, DayType.WORK,
                        activity(Duration.ofHours(1)),
                        activity(Duration.ofHours(3), "other_project", "comment 1"),
                        activity(Duration.ofHours(3), null, "comment 2")));
        assertThat(tmpOutStream).hasToString(lines(
                "Date,Project,TimePerProject,TimePerDay,Comment",
                "2021-06-04,,,07:00,day comment",
                ",Project9FromOuterSpace,01:00,,abc",
                ",other_project,03:00,,comment 1",
                ",,03:00,,comment 2"));
    }

    private static String lines(String... elements)
    {
        final String nl = System.lineSeparator();
        return String.join(nl, elements) + nl;
    }

    @Test
    void exportNullDay()
    {
        runExport(false, Collections.singletonList(null));
        assertThat(tmpOutStream).hasToString(lines("Date,Project,TimePerProject,TimePerDay,Comment"));
    }

    @Test
    void exportEmptyDay()
    {
        final LocalDate dateOne = LocalDate.of(2021, Month.JUNE, 4);
        runExport(false, day(dateOne, DayType.WORK));
        assertThat(tmpOutStream)
                .hasToString(lines(
                        "Date,Project,TimePerProject,TimePerDay,Comment",
                        "2021-06-04,,,00:00,day comment"));
    }

    private void runExport(boolean filterForWeekDays, ProjectReportDay day)
    {
        runExport(filterForWeekDays, Collections.singletonList(day));
    }

    private void runExport(boolean filterForWeekDays, List<ProjectReportDay> days)
    {
        final CSVProjectReportExporter projectReportExporter = new CSVProjectReportExporter(
                createTestConfig(filterForWeekDays), outStreamProvider);
        projectReportExporter.export(projectReport(days), progressMonitorMock);
    }

    List<ProjectReportDay> createDays()
    {
        final LocalDate dateOne = LocalDate.of(2021, Month.JUNE, 4);
        final LocalDate dateTwo = LocalDate.of(2021, Month.JUNE, 5);
        return List.of(
                day(dateOne, DayType.WORK, activity(Duration.ofHours(1))),
                day(dateTwo, DayType.WEEKEND, activity(Duration.ofHours(1))));
    }

    private ProjectReportImpl projectReport(List<ProjectReportDay> days)
    {
        return new ProjectReportImpl(YearMonth.of(2021, Month.JUNE), days, emptyList());
    }

    private ProjectReportDay day(LocalDate date, DayType type, ProjectReportActivity... projects)
    {
        return new ProjectReportImpl.DayImpl(date, type, "day comment", asList(projects));
    }

    private ProjectReportDay day(LocalDate date, DayType type)
    {
        return new ProjectReportImpl.DayImpl(date, type, "day comment", null);
    }

    private ProjectReportActivity activity(Duration workingTime)
    {
        final String PROJECT_ID = "Project9FromOuterSpace";
        final String COMMENT = "abc";
        return activity(workingTime, PROJECT_ID, COMMENT);
    }

    private ProjectReportActivity activity(Duration workingTime, String projectId, String comment)
    {
        return new ProjectReportImpl.ProjectActivityImpl(
                new ProjectImpl(null, projectId, null), workingTime, List.of(comment));
    }

    CSVConfig createTestConfig(boolean filterForWeekDays)
    {
        return new CSVConfig(new PluginConfiguration()
        {
            @Override
            public String getMandatoryValue(String propertyName)
            {
                return null;
            }

            @Override
            public Optional<String> getOptionalValue(String propertyName)
            {
                if ("filter_for_weekdays".equalsIgnoreCase(propertyName))
                {
                    return Optional.of(filterForWeekDays ? "True" : "False");
                }
                return Optional.empty();
            }

            @Override
            public Path getDataDir()
            {
                return null;
            }
        });
    }
}
