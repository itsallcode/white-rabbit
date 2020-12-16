package org.itsallcode.whiterabbit.logic.report.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayActivities;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectReportGeneratorTest
{
    private static final YearMonth MONTH = YearMonth.of(2020, Month.DECEMBER);
    private static final LocalDate DATE1 = MONTH.atDay(1);
    private static final LocalDate DATE2 = MONTH.atDay(2);
    private static final Project PROJECT1 = new Project("p1", "Project 1", "P01");
    private static final Project PROJECT2 = new Project("p2", "Project 2", "P02");

    @Mock
    Storage storageMock;
    private ProjectReportGenerator reportGenerator;

    @BeforeEach
    void setUp()
    {
        reportGenerator = new ProjectReportGenerator(storageMock);
    }

    @Test
    void emptyReportForMissingMonth()
    {
        when(storageMock.loadMonth(MONTH)).thenReturn(Optional.empty());

        assertThat(reportGenerator.generateReport(MONTH).days).isEmpty();
    }

    @Test
    void emptyReportForMonthWithoutDays()
    {
        simulateDays();

        assertThat(reportGenerator.generateReport(MONTH).days).isEmpty();
    }

    @Test
    void dayWithoutActivity()
    {
        simulateDays(day(DATE1, DayType.WORK));

        final List<Day> days = reportGenerator.generateReport(MONTH).days;
        assertThat(days).hasSize(1);
        assertThat(days.get(0).date).isEqualTo(DATE1);
        assertThat(days.get(0).type).isEqualTo(DayType.WORK);
        assertThat(days.get(0).projects).isEmpty();
    }

    @Test
    void dayWithActivity()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, Duration.ofHours(2))));

        final List<Day> days = reportGenerator.generateReport(MONTH).days;
        final List<ProjectActivity> projects = days.get(0).projects;
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).project).isSameAs(PROJECT1);
        assertThat(projects.get(0).workingTime).hasHours(2);
    }

    @Test
    void activityWithoutProject()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(null, Duration.ofHours(2))));

        assertThat(reportGenerator.generateReport(MONTH).days.get(0).projects).isEmpty();
    }

    @Test
    void activityWithoutDuration()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, null)));

        final List<Day> days = reportGenerator.generateReport(MONTH).days;
        final List<ProjectActivity> projects = days.get(0).projects;
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).project).isSameAs(PROJECT1);
        assertThat(projects.get(0).workingTime).isZero();
    }

    @Test
    void dayWithTwoProjectsReturnsTwoProjects()
    {
        simulateDays(day(DATE1, DayType.WORK,
                activity(PROJECT1, Duration.ofHours(2)),
                activity(PROJECT2, Duration.ofHours(3))));

        final List<Day> days = reportGenerator.generateReport(MONTH).days;
        final List<ProjectActivity> projects = days.get(0).projects;
        assertThat(projects).hasSize(2);
        assertThat(projects.get(0).project).isSameAs(PROJECT1);
        assertThat(projects.get(0).workingTime).hasHours(2);
        assertThat(projects.get(1).project).isSameAs(PROJECT2);
        assertThat(projects.get(1).workingTime).hasHours(3);
    }

    @Test
    void dayWithTwoActivitiesForSameProjectAggregatesDuration()
    {
        simulateDays(day(DATE1, DayType.WORK,
                activity(PROJECT1, Duration.ofHours(2)),
                activity(PROJECT1, Duration.ofHours(3))));

        final List<Day> days = reportGenerator.generateReport(MONTH).days;
        final List<ProjectActivity> projects = days.get(0).projects;
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).project).isSameAs(PROJECT1);
        assertThat(projects.get(0).workingTime).hasHours(5);
    }

    @Test
    void twoDaysWithSameProject()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, Duration.ofHours(2))),
                day(DATE2, DayType.WORK, activity(PROJECT1, Duration.ofHours(3))));

        final List<Day> days = reportGenerator.generateReport(MONTH).days;
        assertThat(days).hasSize(2);
        assertThat(days.get(0).date).isEqualTo(DATE1);
        assertThat(days.get(0).type).isEqualTo(DayType.WORK);
        assertThat(days.get(1).date).isEqualTo(DATE2);
        assertThat(days.get(1).type).isEqualTo(DayType.WORK);

        List<ProjectActivity> projects = days.get(0).projects;
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).project).isSameAs(PROJECT1);
        assertThat(projects.get(0).workingTime).hasHours(2);

        projects = days.get(1).projects;
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).project).isSameAs(PROJECT1);
        assertThat(projects.get(0).workingTime).hasHours(3);
    }

    private Activity activity(Project project, Duration duration)
    {
        final Activity activityMock = mock(Activity.class);
        when(activityMock.getProject()).thenReturn(project);
        lenient().when(activityMock.getDuration()).thenReturn(duration);
        return activityMock;
    }

    private DayRecord day(LocalDate date, DayType type, Activity... activities)
    {
        final DayRecord dayRecordMock = mock(DayRecord.class);
        final DayActivities activitiesMock = mock(DayActivities.class);
        when(activitiesMock.getAll()).thenReturn(Arrays.asList(activities));
        when(dayRecordMock.getDate()).thenReturn(date);
        when(dayRecordMock.getType()).thenReturn(type);
        when(dayRecordMock.activities()).thenReturn(activitiesMock);
        return dayRecordMock;
    }

    private void simulateDays(DayRecord... days)
    {
        final MonthIndex monthIndexMock = mock(MonthIndex.class);
        when(monthIndexMock.getSortedDays()).thenReturn(Arrays.stream(days));
        when(storageMock.loadMonth(MONTH)).thenReturn(Optional.of(monthIndexMock));
    }
}
