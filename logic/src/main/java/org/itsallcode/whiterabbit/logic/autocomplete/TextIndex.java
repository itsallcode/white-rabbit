package org.itsallcode.whiterabbit.logic.autocomplete;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

class TextIndex implements AutocompleteEntrySupplier
{
    private static final Logger LOG = LogManager.getLogger(TextIndex.class);
    private static final int MAX_RESULTS = 10;

    private final Map<String, List<String>> lowerCaseIndex;
    private final SortedSet<String> lowerCaseValues;

    private TextIndex(Map<String, List<String>> lowerCaseIndex, SortedSet<String> lowerCaseValues)
    {
        this.lowerCaseIndex = lowerCaseIndex;
        this.lowerCaseValues = lowerCaseValues;
    }

    static TextIndex build(Collection<String> entries)
    {
        List<String> uniqueEntries = entries.stream().distinct().collect(toList());
        LOG.debug("Creating autocompleter for {} entries ({} unique): {}", entries.size(), uniqueEntries.size(),
                uniqueEntries);
        final Map<String, List<String>> lowerCaseIndex = uniqueEntries.stream()
                .collect(groupingBy(String::toLowerCase));
        final SortedSet<String> lowerCaseValues = new TreeSet<>(lowerCaseIndex.keySet());
        return new TextIndex(lowerCaseIndex, lowerCaseValues);
    }

    @Override
    public List<AutocompleteProposal> getEntries(String searchText)
    {
        if (searchText == null || searchText.isBlank())
        {
            return createProposals(lowerCaseValues, "");
        }
        final SortedSet<String> lowerCaseMatches = lowerCaseValues.subSet(searchText.toLowerCase(),
                searchText.toLowerCase() + Character.MAX_VALUE);
        return createProposals(lowerCaseMatches, searchText);
    }

    private List<AutocompleteProposal> createProposals(SortedSet<String> lowerCaseMatches, String searchText)
    {
        return lowerCaseMatches.stream()
                .map(lowerCaseIndex::get)
                .flatMap(List::stream)
                .map(proposedText -> createProposal(searchText, proposedText, 0))
                .sorted()
                .limit(MAX_RESULTS)
                .collect(toList());
    }

    private AutocompleteProposal createProposal(String searchText, String proposedText, int priority)
    {
        final int matchPositionStart = proposedText.toLowerCase().indexOf(searchText.toLowerCase());
        return new AutocompleteProposal(proposedText, priority, matchPositionStart, searchText.length());
    }

}
