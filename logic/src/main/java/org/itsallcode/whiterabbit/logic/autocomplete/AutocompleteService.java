package org.itsallcode.whiterabbit.logic.autocomplete;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayActivities;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.itsallcode.whiterabbit.logic.storage.CachingStorage;

public class AutocompleteService
{
    private static final Logger LOG = LogManager.getLogger(AutocompleteService.class);

    private static final Period MAX_AGE = Period.ofMonths(2);
    private final CachingStorage storage;
    private final ClockService clockService;

    private final CachingAutocompleter dayCommentAutocompleter;
    private final CachingAutocompleter activityCommentAutocompleter;

    public AutocompleteService(final CachingStorage storage, final ClockService clockService, final Locale locale)
    {
        this.clockService = clockService;
        this.dayCommentAutocompleter = new CachingAutocompleter(this::getDayComments, locale);
        this.activityCommentAutocompleter = new CachingAutocompleter(this::getActivityComments, locale);
        this.storage = storage;
        this.storage.addCacheInvalidationListener(this::invalidateCache);
    }

    private void invalidateCache(final MonthIndex updatedMonth)
    {
        dayCommentAutocompleter.invalidateCache();
        activityCommentAutocompleter.invalidateCache();
    }

    public AutocompleteEntrySupplier dayCommentAutocompleter()
    {
        return dayCommentAutocompleter;
    }

    public AutocompleteEntrySupplier activityCommentAutocompleter()
    {
        return activityCommentAutocompleter;
    }

    private List<String> getDayComments()
    {
        return getLatestDays().stream()
                .map(DayRecord::getComment)
                .filter(Objects::nonNull)
                .filter(comment -> !comment.isBlank())
                .toList();
    }

    private List<String> getActivityComments()
    {
        return getActivities()
                .map(Activity::getComment)
                .filter(Objects::nonNull)
                .filter(comment -> !comment.isBlank())
                .toList();
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

    public Optional<ProjectImpl> getSuggestedProject()
    {
        final List<ProjectImpl> projects = getActivities().map(Activity::getProject)
                .filter(Objects::nonNull)
                .toList();
        final Map<String, List<ProjectImpl>> groupedProjects = projects.stream()
                .collect(groupingBy(ProjectImpl::getProjectId));
        final Map<String, Long> frequencyMap = projects.stream()
                .map(ProjectImpl::getProjectId)
                .collect(groupingBy(identity(), counting()));
        final Optional<ProjectImpl> mostFrequentlyUsedProject = frequencyMap
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(projectId -> groupedProjects.get(projectId).get(0));
        LOG.trace("Project frequency: {}, most frequently: {}", frequencyMap, mostFrequentlyUsedProject);
        return mostFrequentlyUsedProject;
    }

    private static class CachingAutocompleter implements AutocompleteEntrySupplier
    {
        private TextIndex index;
        private final Supplier<List<String>> availableTextSupplier;
        private final Locale locale;

        private CachingAutocompleter(final Supplier<List<String>> availableTextSupplier, final Locale locale)
        {
            this.availableTextSupplier = availableTextSupplier;
            this.locale = locale;
        }

        private void invalidateCache()
        {
            index = null;
        }

        @Override
        public List<AutocompleteProposal> getEntries(final String prompt)
        {
            if (index == null)
            {
                index = TextIndex.build(availableTextSupplier.get(), locale);
            }
            return index.getEntries(prompt);
        }
    }
}
