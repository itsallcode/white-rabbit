package org.itsallcode.whiterabbit.logic.autocomplete;

import java.util.List;

public interface AutocompleteEntrySupplier
{
    List<AutocompleteProposal> getEntries(String currentText);
}