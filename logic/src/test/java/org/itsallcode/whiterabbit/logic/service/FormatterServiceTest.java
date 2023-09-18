package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.*;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FormatterServiceTest
{
    private FormatterService formatter;

    @BeforeEach
    void setUp()
    {
        formatter = new FormatterService(Locale.GERMANY, ZoneId.of("Europe/Berlin"));
    }

    @Test
    void testFormatDuration()
    {
        assertThat(formatter.format(Duration.ofHours(4).plusMinutes(42).plusSeconds(31))).isEqualTo("04:42");
    }

    @ParameterizedTest
    @CsvSource(
    {
            "PT0S, 00:00",
            "PT-20S, 00:00",
            "PT20S, 00:00",
            "PT4M, 00:04",
            "PT4M20S, 00:04",
            "PT-4M, -00:04",
            "PT-4M-20S, -00:04",
            "PT-4M20S, -00:03",
            "PT14M, 00:14",
            "PT100M, 01:40",
            "PT3H, 03:00",
            "PT13H, 13:00",
            "PT2H34M, 02:34",
            "PT25H34M, 25:34",
            "PT101H34M, 101:34",
    })
    void testFormatDuration2(Duration duration, String expectedValue)
    {
        assertThat(formatter.format(duration)).isEqualTo(expectedValue);
    }

    @Test
    void testFormatDateAndTime()
    {
        assertThat(formatter.formatDateAndTime(Instant.parse("2007-12-03T10:15:30.00Z")))
                .isEqualTo("Mo 03.12.2007 CW49, 11:15:30");
    }
}
