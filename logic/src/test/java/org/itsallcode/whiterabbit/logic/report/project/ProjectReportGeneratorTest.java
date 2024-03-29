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

import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayActivities;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
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
    private static final ProjectImpl PROJECT1 = new ProjectImpl("p1", "Project 1", "P01");
    private static final ProjectImpl PROJECT2 = new ProjectImpl("p2", "Project 2", "P02");

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

        assertThat(reportGenerator.generateReport(MONTH).getDays()).isEmpty();
    }

    @Test
    void emptyMonthlyReportForMissingMonth()
    {
        when(storageMock.loadMonth(MONTH)).thenReturn(Optional.empty());

        assertThat(reportGenerator.generateReport(MONTH).getProjects()).isEmpty();
    }

    @Test
    void emptyReportForMonthWithoutDays()
    {
        simulateDays();

        assertThat(reportGenerator.generateReport(MONTH).getDays()).isEmpty();
    }

    @Test
    void emptyMonthlyReportForMonthWithoutDays()
    {
        simulateDays();

        assertThat(reportGenerator.generateReport(MONTH).getProjects()).isEmpty();
    }

    @Test
    void reportMonth()
    {
        simulateDays(day(DATE1, DayType.WORK));

        assertThat(reportGenerator.generateReport(MONTH).getMonth()).isEqualTo(MONTH);
    }

    @Test
    void dayWithoutActivity()
    {
        simulateDays(day(DATE1, DayType.WORK));

        final List<ProjectReportDay> days = reportGenerator.generateReport(MONTH).getDays();
        assertThat(days).hasSize(1);
        assertThat(days.get(0).getDate()).isEqualTo(DATE1);
        assertThat(days.get(0).getType()).isEqualTo(DayType.WORK);
        assertThat(days.get(0).getProjects()).isEmpty();
    }

    @Test
    void monthlyReportForDayWithoutActivity()
    {
        simulateDays(day(DATE1, DayType.WORK));

        final List<ProjectReportActivity> projects = reportGenerator.generateReport(MONTH).getProjects();
        assertThat(projects).isEmpty();
    }

    @Test
    void dayWithActivity()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, Duration.ofHours(2))));

        final List<ProjectReportDay> days = reportGenerator.generateReport(MONTH).getDays();
        final List<ProjectReportActivity> projects = days.get(0).getProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(2);
        assertThat(projects.get(0).getComments()).isEmpty();
    }

    @Test
    void dayWithActivityAndComment()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, Duration.ofHours(2), "p1")));

        final List<ProjectReportDay> days = reportGenerator.generateReport(MONTH).getDays();
        final List<ProjectReportActivity> projects = days.get(0).getProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(2);
        assertThat(projects.get(0).getComments()).containsExactly("p1");
    }

    @Test
    void monthlyReportForDayWithActivity()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, Duration.ofHours(2), "p1")));

        final List<ProjectReportActivity> projects = reportGenerator.generateReport(MONTH).getProjects();
        assertThat(projects).hasSize(1);

        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(2);
        assertThat(projects.get(0).getComments()).containsExactly("p1");
    }

    @Test
    void activityWithoutProject()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(null, Duration.ofHours(2))));

        assertThat(reportGenerator.generateReport(MONTH).getDays().get(0).getProjects()).isEmpty();
    }

    @Test
    void monthlyReportForActivityWithoutProject()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(null, Duration.ofHours(2))));

        assertThat(reportGenerator.generateReport(MONTH).getProjects()).isEmpty();
    }

    @Test
    void activityWithoutDuration()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, null)));

        final List<ProjectReportDay> days = reportGenerator.generateReport(MONTH).getDays();
        final List<ProjectReportActivity> projects = days.get(0).getProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).isZero();
    }

    @Test
    void monthlyReportForActivityWithoutDuration()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, null)));

        final List<ProjectReportActivity> projects = reportGenerator.generateReport(MONTH).getProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).isZero();
    }

    @Test
    void dayWithTwoProjectsReturnsTwoProjects()
    {
        simulateDays(day(DATE1, DayType.WORK,
                activity(PROJECT1, Duration.ofHours(2), "p1"),
                activity(PROJECT2, Duration.ofHours(3), "p2")));

        final List<ProjectReportDay> days = reportGenerator.generateReport(MONTH).getDays();
        final List<ProjectReportActivity> projects = days.get(0).getProjects();
        assertThat(projects).hasSize(2);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(2);
        assertThat(projects.get(0).getComments()).containsExactly("p1");
        assertThat(projects.get(1).getProject()).isSameAs(PROJECT2);
        assertThat(projects.get(1).getWorkingTime()).hasHours(3);
        assertThat(projects.get(1).getComments()).containsExactly("p2");
    }

    @Test
    void monthlyReportForDayWithTwoProjectsReturnsTwoProjects()
    {
        simulateDays(day(DATE1, DayType.WORK,
                activity(PROJECT1, Duration.ofHours(2), "p1"),
                activity(PROJECT2, Duration.ofHours(3), "p2")));

        final List<ProjectReportActivity> projects = reportGenerator.generateReport(MONTH).getProjects();
        assertThat(projects).hasSize(2);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(2);
        assertThat(projects.get(0).getComments()).containsExactly("p1");
        assertThat(projects.get(1).getProject()).isSameAs(PROJECT2);
        assertThat(projects.get(1).getWorkingTime()).hasHours(3);
        assertThat(projects.get(1).getComments()).containsExactly("p2");
    }

    @Test
    void dayWithTwoActivitiesForSameProjectAggregatesDuration()
    {
        simulateDays(day(DATE1, DayType.WORK,
                activity(PROJECT1, Duration.ofHours(2), "a1"),
                activity(PROJECT1, Duration.ofHours(3), "a2")));

        final List<ProjectReportDay> days = reportGenerator.generateReport(MONTH).getDays();
        final List<ProjectReportActivity> projects = days.get(0).getProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(5);
        assertThat(projects.get(0).getComments()).containsExactly("a1", "a2");
    }

    @Test
    void monthlyReportForDayWithTwoActivitiesForSameProjectAggregatesDuration()
    {
        simulateDays(day(DATE1, DayType.WORK,
                activity(PROJECT1, Duration.ofHours(2), "a1"),
                activity(PROJECT1, Duration.ofHours(3), "a2")));

        final List<ProjectReportDay> days = reportGenerator.generateReport(MONTH).getDays();
        final List<ProjectReportActivity> projects = days.get(0).getProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(5);
        assertThat(projects.get(0).getComments()).containsExactly("a1", "a2");
    }

    @Test
    void twoDaysWithSameProject()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, Duration.ofHours(2), "a1")),
                day(DATE2, DayType.WORK, activity(PROJECT1, Duration.ofHours(3), "a2")));

        final List<ProjectReportDay> days = reportGenerator.generateReport(MONTH).getDays();
        assertThat(days).hasSize(2);
        assertThat(days.get(0).getDate()).isEqualTo(DATE1);
        assertThat(days.get(0).getType()).isEqualTo(DayType.WORK);
        assertThat(days.get(1).getDate()).isEqualTo(DATE2);
        assertThat(days.get(1).getType()).isEqualTo(DayType.WORK);

        List<ProjectReportActivity> projects = days.get(0).getProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(2);
        assertThat(projects.get(0).getComments()).containsExactly("a1");

        projects = days.get(1).getProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(3);
        assertThat(projects.get(0).getComments()).containsExactly("a2");
    }

    @Test
    void monthlyReportForTwoDaysWithSameProject()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, Duration.ofHours(2), "a1")),
                day(DATE2, DayType.WORK, activity(PROJECT1, Duration.ofHours(3), "a2")));

        final List<ProjectReportActivity> projects = reportGenerator.generateReport(MONTH).getProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getProject()).isSameAs(PROJECT1);
        assertThat(projects.get(0).getWorkingTime()).hasHours(5);
        assertThat(projects.get(0).getComments()).containsExactly("a1", "a2");
    }

    @Test
    void monthlyReportRemovesDuplicateComments()
    {
        simulateDays(day(DATE1, DayType.WORK, activity(PROJECT1, Duration.ofHours(2), "a1")),
                day(DATE2, DayType.WORK, activity(PROJECT1, Duration.ofHours(3), "a1")));

        final List<ProjectReportActivity> projects = reportGenerator.generateReport(MONTH).getProjects();
        assertThat(projects.get(0).getComments()).containsExactly("a1");
    }

    private Activity activity(ProjectImpl project, Duration duration)
    {
        return activity(project, duration, null);
    }

    private Activity activity(ProjectImpl project, Duration duration, String comment)
    {
        final Activity activityMock = mock(Activity.class);
        when(activityMock.getProject()).thenReturn(project);

        lenient().when(activityMock.getDuration()).thenReturn(duration);
        lenient().when(activityMock.getComment()).thenReturn(comment);
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
