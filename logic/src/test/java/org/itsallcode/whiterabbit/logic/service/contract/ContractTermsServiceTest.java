package org.itsallcode.whiterabbit.logic.service.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.itsallcode.whiterabbit.logic.test.TestingConfig;
import org.itsallcode.whiterabbit.logic.test.TestingConfig.Builder;
import org.junit.jupiter.api.Test;

class ContractTermsServiceTest
{

    @Test
    void getContractedWorkingTimePerDayReturnsDefault()
    {
        assertThat(create(TestingConfig.builder()).getContractedWorkingTimePerDay()).hasHours(8);
    }

    @Test
    void getCurrentWorkingTimePerDayReturnsDefaultWhenNothingIsConfigured()
    {
        assertThat(create(TestingConfig.builder().withCurrentHoursPerDay(null)).getCurrentWorkingTimePerDay())
                .hasHours(8);
    }

    @Test
    void getCurrentWorkingTimePerDayReturnsCustomValueWhenConfigurationsIsSet()
    {
        assertThat(create(TestingConfig.builder().withCurrentHoursPerDay(Duration.ofHours(4)))
                .getCurrentWorkingTimePerDay()).hasHours(4);
    }

    private ContractTermsService create(Builder builder)
    {
        return new ContractTermsService(builder.build().getHoursPerDayProvider());
    }

}
