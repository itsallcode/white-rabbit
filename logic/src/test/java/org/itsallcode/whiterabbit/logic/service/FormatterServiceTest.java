package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormatterServiceTest
{
    private FormatterService formatter;

    @BeforeEach
    void setUp()
    {
        formatter = new FormatterService(Locale.GERMAN, ZoneId.of("Europe/Berlin"));
    }

    @Test
    void testFormatDuration()
    {
        assertThat(formatter.format(Duration.ofHours(4).plusMinutes(42).plusSeconds(31))).isEqualTo("04:42");
    }

    @Test
    void testFormatDateAndTime()
    {
        assertThat(formatter.formatDateAndTime(Instant.parse("2007-12-03T10:15:30.00Z")))
                .isEqualTo("03.12.07, 11:15:30");
    }
}
