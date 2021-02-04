package org.itsallcode.whiterabbit.logic.service.project;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class ProjectImplTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.simple().forClass(ProjectImpl.class).verify();
    }
}
