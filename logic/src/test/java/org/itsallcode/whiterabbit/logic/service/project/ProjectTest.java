package org.itsallcode.whiterabbit.logic.service.project;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class ProjectTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.simple().forClass(Project.class).verify();
    }
}
