package org.itsallcode.whiterabbit.logic.autocomplete;

import static java.util.Collections.emptyList;
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

    private final Map<String, List<String>> lowerCaseIndex;
    private final SortedSet<String> lowerCaseValues;

    private TextIndex(Map<String, List<String>> lowerCaseIndex, SortedSet<String> lowerCaseValues)
    {
        this.lowerCaseIndex = lowerCaseIndex;
        this.lowerCaseValues = lowerCaseValues;
    }

    static TextIndex build(Collection<String> entries)
    {
        LOG.debug("Creating autocompleter for {} entries: {}", entries.size(), entries);
        final Map<String, List<String>> lowerCaseIndex = entries.stream().collect(groupingBy(String::toLowerCase));
        final SortedSet<String> lowerCaseValues = new TreeSet<>(lowerCaseIndex.keySet());
        return new TextIndex(lowerCaseIndex, lowerCaseValues);
    }

    @Override
    public List<AutocompleteProposal> getEntries(String searchText)
    {
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
    }

    private AutocompleteProposal createProposal(String searchText, String proposedText)
    {
        final int matchPositionStart = proposedText.toLowerCase().indexOf(searchText.toLowerCase());
        return new AutocompleteProposal(proposedText, matchPositionStart, searchText.length());
    }

}
