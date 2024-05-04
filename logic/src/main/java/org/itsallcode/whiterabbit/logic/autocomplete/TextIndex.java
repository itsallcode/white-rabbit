package org.itsallcode.whiterabbit.logic.autocomplete;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final class TextIndex implements AutocompleteEntrySupplier
{
    private static final Logger LOG = LogManager.getLogger(TextIndex.class);
    private static final int MAX_RESULTS = 10;

    private final Map<String, List<String>> lowerCaseIndex;
    private final SortedSet<String> lowerCaseValues;
    private final Map<String, Long> lowerCaseFrequency;
    private final Locale locale;

    private TextIndex(final Map<String, List<String>> lowerCaseIndex, final SortedSet<String> lowerCaseValues,
            final Map<String, Long> lowerCaseFrequency, final Locale locale)
    {
        this.lowerCaseIndex = new HashMap<>(lowerCaseIndex);
        this.lowerCaseValues = new TreeSet<>(lowerCaseValues);
        this.lowerCaseFrequency = new HashMap<>(lowerCaseFrequency);
        this.locale = locale;
    }

    static TextIndex build(final Collection<String> entries, final Locale locale)
    {
        final List<String> uniqueEntries = entries.stream().distinct().toList();

        final Map<String, List<String>> lowerCaseIndex = uniqueEntries.stream()
                .collect(groupingBy(value -> value.toLowerCase(locale)));
        final SortedSet<String> lowerCaseValues = new TreeSet<>(lowerCaseIndex.keySet());
        final Map<String, Long> lowerCaseFrequency = entries.stream().map(value -> value.toLowerCase(locale))
                .collect(groupingBy(identity(), counting()));
        LOG.trace("Creating autocompleter for {} entries ({} unique): {}, frequencies: {}", entries.size(),
                uniqueEntries.size(),
                uniqueEntries, lowerCaseFrequency);
        return new TextIndex(lowerCaseIndex, lowerCaseValues, lowerCaseFrequency, locale);
    }

    private String toLowerCase(final String value)
    {
        return value.toLowerCase(locale);
    }

    @Override
    public List<AutocompleteProposal> getEntries(final String searchText)
    {
        if (searchText == null)
        {
            return createProposals(lowerCaseValues, "");
        }
        if (searchText.isBlank())
        {
            return createProposals(lowerCaseValues, searchText);
        }
        final SortedSet<String> lowerCaseMatches = lowerCaseValues.subSet(toLowerCase(searchText),
                toLowerCase(searchText) + Character.MAX_VALUE);
        return createProposals(lowerCaseMatches, searchText);
    }

    private List<AutocompleteProposal> createProposals(final SortedSet<String> lowerCaseMatches,
            final String searchText)
    {
        return lowerCaseMatches.stream()
                .map(lowerCaseIndex::get)
                .flatMap(List::stream)
                .map(proposedText -> createProposal(searchText, proposedText))
                .sorted()
                .limit(MAX_RESULTS)
                .toList();
    }

    private AutocompleteProposal createProposal(final String searchText, final String proposedText)
    {
        final int matchPositionStart = toLowerCase(proposedText).indexOf(toLowerCase(searchText));
        final long priority = lowerCaseFrequency.getOrDefault(toLowerCase(proposedText), 0L);
        LOG.trace("Create proposal for '{}'. Proposal: {}, priority: {}", searchText, proposedText, priority);
        return new AutocompleteProposal(proposedText, priority, matchPositionStart, searchText.length());
    }
}
