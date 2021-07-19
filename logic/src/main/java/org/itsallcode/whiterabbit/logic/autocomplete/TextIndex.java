package org.itsallcode.whiterabbit.logic.autocomplete;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class TextIndex implements AutocompleteEntrySupplier
{
    private static final Logger LOG = LogManager.getLogger(TextIndex.class);
    private static final int MAX_RESULTS = 10;

    private final Map<String, List<String>> lowerCaseIndex;
    private final SortedSet<String> lowerCaseValues;
    private final Map<String, Long> lowerCaseFrequency;

    private TextIndex(Map<String, List<String>> lowerCaseIndex, SortedSet<String> lowerCaseValues,
            Map<String, Long> lowerCaseFrequency)
    {
        this.lowerCaseIndex = lowerCaseIndex;
        this.lowerCaseValues = lowerCaseValues;
        this.lowerCaseFrequency = lowerCaseFrequency;
    }

    static TextIndex build(Collection<String> entries)
    {
        final List<String> uniqueEntries = entries.stream().distinct().collect(toList());

        final Map<String, List<String>> lowerCaseIndex = uniqueEntries.stream()
                .collect(groupingBy(String::toLowerCase));
        final SortedSet<String> lowerCaseValues = new TreeSet<>(lowerCaseIndex.keySet());
        final Map<String, Long> lowerCaseFrequency = entries.stream().map(String::toLowerCase)
                .collect(groupingBy(identity(), counting()));
        LOG.trace("Creating autocompleter for {} entries ({} unique): {}, frequencies: {}", entries.size(),
                uniqueEntries.size(),
                uniqueEntries, lowerCaseFrequency);
        return new TextIndex(lowerCaseIndex, lowerCaseValues, lowerCaseFrequency);
    }

    @Override
    public List<AutocompleteProposal> getEntries(String searchText)
    {
        if (searchText == null)
        {
            return createProposals(lowerCaseValues, "");
        }
        if (searchText.isBlank())
        {
            return createProposals(lowerCaseValues, searchText);
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
                .map(proposedText -> createProposal(searchText, proposedText))
                .sorted()
                .limit(MAX_RESULTS)
                .collect(toList());
    }

    private AutocompleteProposal createProposal(String searchText, String proposedText)
    {
        final int matchPositionStart = proposedText.toLowerCase().indexOf(searchText.toLowerCase());
        final long priority = lowerCaseFrequency.getOrDefault(proposedText.toLowerCase(), 0L);
        LOG.trace("Create proposal for '{}'. Proposal: {}, priority: {}", searchText, proposedText, priority);
        return new AutocompleteProposal(proposedText, priority, matchPositionStart, searchText.length());
    }
}
