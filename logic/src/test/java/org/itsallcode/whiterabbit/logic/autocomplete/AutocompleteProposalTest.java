package org.itsallcode.whiterabbit.logic.autocomplete;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class AutocompleteProposalTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass(AutocompleteProposal.class).verify();
    }
}
