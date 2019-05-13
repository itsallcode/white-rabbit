package org.itsallcode.whiterabbit.logic.service.scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FullMinuteTriggerTest
{
    private static final Instant NOW = Instant.parse("2007-12-03T10:15:30.00Z");

    private FullMinuteTrigger trigger;

    @BeforeEach
    void setUp()
    {
        trigger = new FullMinuteTrigger();
    }

    @Test
    void testNoContextReturnsNow()
    {
        assertThat(trigger.nextExecutionTime(NOW, Optional.empty())).isEqualTo(NOW);
    }

    @Test
    void testContextReturnsNextMinute()
    {
        assertThat(trigger.nextExecutionTime(NOW,
                Optional.of(new TriggerContext(null, null, NOW.minusSeconds(60)))))
                        .isEqualTo(NOW.plusSeconds(30));
    }
}
