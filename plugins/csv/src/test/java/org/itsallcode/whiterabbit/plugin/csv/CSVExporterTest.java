package org.itsallcode.whiterabbit.plugin.csv;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;

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
    void setUp() throws IOException {
        tmpOutStream = new ByteArrayOutputStream();
        when(outStreamProvider.getStream(Mockito.anyString())).thenReturn(tmpOutStream);
    }

    @Test
    void exportCSVFilterForWorkdays()
    {
        runExport(true, createDays());
        assertThat(tmpOutStream.toString()).isEqualTo("Date,Project,Time,Comment\n2021-06-04,,,day comment\n,Project9FromOuterSpace,01:00,abc\n");
    }

    @Test
    void exportCSVNoFilter()
    {
        runExport(false, createDays());
        assertThat(tmpOutStream.toString()).isEqualTo("Date,Project,Time,Comment\n2021-06-04,,,day comment\n,Project9FromOuterSpace,01:00,abc\n2021-06-05,,,day comment\n,Project9FromOuterSpace,01:00,abc\n");
    }

    private void runExport(boolean filterForWeekDays, List<ProjectReportDay> days)
    {
        CSVProjectReportExporter projectReportExporter =
                new CSVProjectReportExporter(filterForWeekDays, ",", outStreamProvider);
        projectReportExporter.export(projectReport(days), progressMonitorMock);
    }

    List<ProjectReportDay> createDays() {
        final LocalDate dateOne = LocalDate.of(2021, Month.JUNE, 4);
        final LocalDate dateTwo = LocalDate.of(2021, Month.JUNE, 5);
        return List.of(
                day(dateOne, DayType.WORK, activity(Duration.ofHours(1))),
                day(dateTwo, DayType.WEEKEND, activity(Duration.ofHours(1))));
    }

    private ProjectReportImpl projectReport(List<ProjectReportDay> days)
    {
        return new ProjectReportImpl(YearMonth.of(2021, Month.JUNE), days);
    }

    private ProjectReportDay day(LocalDate date, DayType type, ProjectReportActivity... projects)
    {
        return new ProjectReportImpl.DayImpl(date, type, "day comment", asList(projects));
    }

    private ProjectReportActivity activity(Duration workingTime)
    {
        String PROJECT_ID = "Project9FromOuterSpace";
        String COMMENT = "abc";
        return new ProjectReportImpl.ProjectActivityImpl(
                new ProjectImpl(null, PROJECT_ID, null), workingTime, COMMENT);
    }
}
