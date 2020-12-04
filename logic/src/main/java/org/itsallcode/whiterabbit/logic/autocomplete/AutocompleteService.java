package org.itsallcode.whiterabbit.logic.autocomplete;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayActivities;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.logic.storage.CachingStorage;

public class AutocompleteService
{
    private static final Logger LOG = LogManager.getLogger(AutocompleteService.class);

    private static final Period MAX_AGE = Period.ofMonths(2);
    private final CachingStorage storage;
    private final ClockService clockService;

    public AutocompleteService(CachingStorage storage, ClockService clockService)
    {
        this.storage = storage;
        this.clockService = clockService;
    }

    public AutocompleteEntrySupplier dayCommentAutocompleter()
    {
        return autocompleter(getDayComments());
    }

    public AutocompleteEntrySupplier activityCommentAutocompleter()
    {
        return autocompleter(getActivityComments());
    }

    private List<String> getDayComments()
    {
        return getLatestDays().stream()
                .map(DayRecord::getComment)
                .filter(Objects::nonNull)
                .filter(comment -> !comment.isBlank())
                .distinct()
                .collect(toList());
    }

    private List<String> getActivityComments()
    {
        return getActivities()
                .map(Activity::getComment)
                .filter(Objects::nonNull)
                .filter(comment -> !comment.isBlank())
                .distinct()
                .collect(toList());
    }

    private Stream<Activity> getActivities()
    {
        return getLatestDays().stream()
                .map(DayRecord::activities)
                .map(DayActivities::getAll)
                .flatMap(List::stream);
    }

    private List<DayRecord> getLatestDays()
    {
        final LocalDate maxAge = clockService.getCurrentDate().minus(MAX_AGE);
        return storage.getLatestDays(maxAge);
    }

    AutocompleteEntrySupplier autocompleter(Collection<String> allEntries)
    {
        LOG.debug("Creating autocompleter for {} entries: {}", allEntries.size(), allEntries);
        final Map<String, List<String>> lowerCaseIndex = allEntries.stream().collect(groupingBy(String::toLowerCase));
        final SortedSet<String> lowerCaseValues = new TreeSet<>(lowerCaseIndex.keySet());
        return searchText -> {
            if (searchText == null || searchText.isBlank())
            {
                return emptyList();
            }
            final SortedSet<String> lowerCaseMatches = lowerCaseValues.subSet(searchText.toLowerCase(),
                    searchText.toLowerCase() + Character.MAX_VALUE);
            return lowerCaseMatches.stream()
                    .map(lowerCaseIndex::get)
                    .flatMap(List::stream)
                    .map(proposedText -> createProposal(searchText, proposedText))
                    .collect(toList());
        };
    }

    private AutocompleteProposal createProposal(String searchText, String proposedText)
    {
        final int matchPositionStart = proposedText.toLowerCase().indexOf(searchText.toLowerCase());
        return new AutocompleteProposal(proposedText, matchPositionStart, searchText.length());
    }

    public Optional<Project> getSuggestedProject()
    {
        final List<Project> projects = getActivities().map(Activity::getProject)
                .filter(Objects::nonNull)
                .collect(toList());
        final Map<String, List<Project>> groupedProjects = projects.stream()
                .filter(Objects::nonNull)
                .collect(groupingBy(Project::getProjectId));
        final Map<String, Long> frequencyMap = projects.stream()
                .map(Project::getProjectId)
                .collect(groupingBy(identity(), counting()));
        final Optional<Project> mostFrequentlyUsedProject = frequencyMap
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(projectId -> groupedProjects.get(projectId).get(0));
        LOG.debug("Project frequency: {}, most frequently: {}", frequencyMap, mostFrequentlyUsedProject);
        return mostFrequentlyUsedProject;
    }
}
