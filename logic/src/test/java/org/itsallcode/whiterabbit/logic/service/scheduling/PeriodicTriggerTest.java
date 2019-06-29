package org.itsallcode.whiterabbit.logic.service.scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class PeriodicTriggerTest
{
    private static final Instant NOW = Instant.parse("2007-12-03T10:15:30.00Z");
    private static final Duration ADDITIONAL_WAIT = Duration.ofMillis(50);

    @Test
    void testEveryMinuteNoContextReturnsNow()
    {
        assertThat(PeriodicTrigger.everyMinute().nextExecutionTime(NOW, Optional.empty()))
                .isEqualTo(NOW);
    }

    @Test
    void testEveryMinuteContextReturnsNextMinute()
    {
        assertThat(PeriodicTrigger.everyMinute().nextExecutionTime(NOW,
                Optional.of(new TriggerContext(null, null, NOW.minusSeconds(60)))))
                        .isEqualTo(NOW.plusSeconds(30).plus(ADDITIONAL_WAIT));
    }

    @Test
    void testEverySecondNoContextReturnsNow()
    {
        assertThat(PeriodicTrigger.everySecond().nextExecutionTime(NOW, Optional.empty()))
                .isEqualTo(NOW);
    }

    @Test
    void testEverySecondContextReturnsNextSecond()
    {
        assertThat(PeriodicTrigger.everySecond().nextExecutionTime(NOW,
                Optional.of(new TriggerContext(null, null, NOW.minusSeconds(1)))))
                        .isEqualTo(NOW.plusSeconds(1).plus(ADDITIONAL_WAIT));
    }
}
