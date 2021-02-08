package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClockServiceTest
{
    private static final Instant NOW = Instant.parse("2007-12-03T10:15:30.00Z");
    private ClockService clockService;

    @BeforeEach
    void setUp()
    {
        clockService = new ClockService(
                Clock.fixed(NOW, ZoneId.of("Europe/Berlin")));
    }

    @Test
    void testGetCurrentDate()
    {
        assertThat(clockService.getCurrentDate()).isEqualTo(LocalDate.of(2007, 12, 3));
    }

    @Test
    void testGetCurrentYearMonth()
    {
        assertThat(clockService.getCurrentYearMonth()).isEqualTo(YearMonth.of(2007, 12));
    }

    @Test
    void testGetCurrentTime()
    {
        assertThat(clockService.getCurrentTime()).isEqualTo(LocalTime.of(11, 15));
    }

    @Test
    void testGetDurationUntilNextFullMinute()
    {
        assertThat(clockService.getDurationUntilNextFullMinute()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void testInstant()
    {
        assertThat(clockService.instant()).isSameAs(NOW);
    }
}
