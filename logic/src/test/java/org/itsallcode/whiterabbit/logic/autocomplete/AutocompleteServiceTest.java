package org.itsallcode.whiterabbit.logic.autocomplete;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayActivities;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.logic.storage.CachingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutocompleteServiceTest
{
    @Mock
    CachingStorage storageMock;
    @Mock
    private ClockService clockServiceMock;

    private AutocompleteService autocompleteService;

    @BeforeEach
    void setUp()
    {
        autocompleteService = new AutocompleteService(storageMock, clockServiceMock);
    }

    @Test
    void dayCommentAutocompleter()
    {
        simulateDays(dayRecordsWithComments(null, "", "Comment A", "Comment B"));
        final List<AutocompleteProposal> entries = autocompleteService.dayCommentAutocompleter().getEntries("comm");

        assertThat(entries).hasSize(2).extracting(AutocompleteProposal::getText).containsExactly("Comment A",
                "Comment B");
        assertThat(entries).extracting(AutocompleteProposal::getMatchPositionStart).containsExactly(0, 0);
        assertThat(entries).extracting(AutocompleteProposal::getMatchLength).containsExactly(4, 4);
    }

    @Test
    void activityCommentAutocompleter()
    {
        simulateDays(createDayRecordsWithActivityComments(null, "", "Comment A", "Comment B"));
        assertThat(autocompleteService.activityCommentAutocompleter().getEntries("comm"))
                .extracting(AutocompleteProposal::getText)
                .containsExactly("Comment A", "Comment B");
    }

    @ParameterizedTest(name = "[{index}] available values {0}, search text ''{1}''")
    @ArgumentsSource(AutocompleterArgumentsProvider.class)
    void autocompleter(List<String> availableEntries, String searchText, List<String> expectedResult)
    {
        final List<AutocompleteProposal> entries = autocompleteService.autocompleter(availableEntries)
                .getEntries(searchText);
        assertThat(entries)
                .as("autocomplete for available values " + availableEntries + " and search text '" + searchText + "'")
                .extracting(AutocompleteProposal::getText)
                .containsExactly(expectedResult.toArray(new String[0]));
    }

    private static class AutocompleterArgumentsProvider implements ArgumentsProvider
    {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context) throws Exception
        {
            return Stream.of(
                    Arguments.of(List.of(), "text", List.of()),
                    Arguments.of(List.of("text"), null, List.of()),
                    Arguments.of(List.of("text"), "", List.of()),
                    Arguments.of(List.of("text"), " ", List.of()),
                    Arguments.of(List.of("TEXT"), "text", List.of("TEXT")),
                    Arguments.of(List.of("match", "nomatch"), "ma", List.of("match")),
                    Arguments.of(List.of("match1", "match2"), "ma", List.of("match1", "match2")),
                    Arguments.of(List.of("first second"), "fi", List.of("first second")),
                    Arguments.of(List.of("first second"), "sec", List.of()));
        }
    }

    @Test
    void getSuggestedProject_returnsEmptyOptional_whenNoDataAvailable()
    {
        simulateDays(createDayRecordsWithActivityProjects());
        assertThat(autocompleteService.getSuggestedProject()).isEmpty();
    }

    @Test
    void getSuggestedProject_singleProject()
    {
        simulateDays(createDayRecordsWithActivityProjects("p1"));
        assertProjectFound("p1");
    }

    @Test
    void getSuggestedProject_mostFrequentProjectReturned()
    {
        simulateDays(createDayRecordsWithActivityProjects("p1", "p2", "p2"));
        assertProjectFound("p2");
    }

    private void assertProjectFound(String expectedProjectId)
    {
        final Optional<Project> suggestedProject = autocompleteService.getSuggestedProject();
        assertThat(suggestedProject).isPresent();
        assertThat(suggestedProject.get().getProjectId()).isEqualTo(expectedProjectId);
    }

    private void simulateDays(final List<DayRecord> days)
    {
        final LocalDate now = LocalDate.of(2020, Month.APRIL, 1);
        final LocalDate maxAge = now.minus(Period.ofMonths(2));
        when(clockServiceMock.getCurrentDate()).thenReturn(now);
        when(storageMock.getLatestDays(maxAge)).thenReturn(days);
    }

    private List<DayRecord> createDayRecordsWithActivityComments(String... comments)
    {
        return Arrays.stream(comments).map(this::createDayRecordWithActivityComment).collect(toList());
    }

    private List<DayRecord> createDayRecordsWithActivityProjects(String... projectIds)
    {
        return Arrays.stream(projectIds).map(this::createDayRecordWithProject).collect(toList());
    }

    private DayRecord createDayRecordWithProject(String projectId)
    {
        final Activity activity = mock(Activity.class);
        final Project project = new Project(projectId, "Project " + projectId, null);
        when(activity.getProject()).thenReturn(project);
        return dayWithActivities(activity);
    }

    private DayRecord createDayRecordWithActivityComment(String comment)
    {
        final Activity activity = mock(Activity.class);
        when(activity.getComment()).thenReturn(comment);
        return dayWithActivities(activity);
    }

    private DayRecord dayWithActivities(Activity... activityList)
    {
        final DayActivities activities = mock(DayActivities.class);
        when(activities.getAll()).thenReturn(asList(activityList));
        final DayRecord dayRecord = mock(DayRecord.class);
        when(dayRecord.activities()).thenReturn(activities);
        return dayRecord;
    }

    private List<DayRecord> dayRecordsWithComments(String... comments)
    {
        return Arrays.stream(comments).map(this::createDayRecord).collect(toList());
    }

    private DayRecord createDayRecord(String comment)
    {
        final DayRecord dayRecord = mock(DayRecord.class);
        when(dayRecord.getComment()).thenReturn(comment);
        return dayRecord;
    }
}