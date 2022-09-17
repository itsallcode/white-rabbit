package org.itsallcode.whiterabbit.logic.autocomplete;

import java.util.List;

@FunctionalInterface
public interface AutocompleteEntrySupplier
{
    List<AutocompleteProposal> getEntries(String currentText);
}