package org.itsallcode.whiterabbit.logic.autocomplete;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayActivities;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.logic.storage.CachingStorage;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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
        return getLatestDays().stream()
                .map(DayRecord::activities)
                .map(DayActivities::getAll)
                .flatMap(List::stream)
                .map(Activity::getComment)
                .filter(Objects::nonNull)
                .filter(comment -> !comment.isBlank())
                .distinct()
                .collect(toList());
    }

    private List<DayRecord> getLatestDays()
    {
        final LocalDate maxAge = clockService.getCurrentDate().minus(MAX_AGE);
        return storage.getLatestDays(maxAge);
    }

    private AutocompleteEntrySupplier autocompleter(Collection<String> allEntries)
    {
        LOG.debug("Creating autocompleter for {} entries: {}", allEntries.size(), allEntries);
        final Map<String, List<String>> lowerCaseIndex = allEntries.stream().collect(groupingBy(String::toLowerCase));
        final SortedSet<String> lowerCaseValues = new TreeSet<>(lowerCaseIndex.keySet());
        return currentText -> {
            final SortedSet<String> lowerCaseMatches = lowerCaseValues.subSet(currentText.toLowerCase(),
                    currentText.toLowerCase() + Character.MAX_VALUE);
            return lowerCaseMatches.stream().map(lowerCaseIndex::get).flatMap(List::stream).collect(toList());
        };
    }

    public Optional<Project> getSuggestedProject()
    {
        return Optional.empty();
    }
}
