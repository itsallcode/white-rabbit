package org.itsallcode.whiterabbit.logic.autocomplete;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class AutocompleteService
{
    private final Storage storage;

    public AutocompleteService(Storage storage)
    {
        this.storage = storage;
    }

    public AutocompleteEntrySupplier dayCommentAutocompleter()
    {
        return autocompleter(asList("blah", "blubb", "abc", "cyasd"));
    }

    public AutocompleteEntrySupplier activityCommentAutocompleter()
    {
        return autocompleter(asList("blah", "blubb", "abc", "cyasd"));
    }

    private AutocompleteEntrySupplier autocompleter(Collection<String> allEntries)
    {
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
