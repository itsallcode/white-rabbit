package org.itsallcode.whiterabbit.jfxui.table.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Locale;

import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DurationStringConverterTest
{
    @ParameterizedTest
    @CsvSource({
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
    void testToString(Duration duration, String expectedValue)
    {
        assertThat(converter().toString(duration)).isEqualTo(expectedValue);
    }

    @ParameterizedTest
    @CsvSource(
    {
            "00:00, PT0S",
            "00:04, PT4M",
            "00:14, PT14M",
            "01:40, PT100M",
            "03:00, PT3H",
            "13:00, PT13H",
            "02:34, PT2H34M",
            "25:34, PT25H34M",
            "101:34, PT101H34M", })
    void fromStringLikeFormatted(String input, Duration expectedDuration)
    {
        assertThat(converter().fromString(input)).isEqualTo(expectedDuration);
    }

    @ParameterizedTest
    @CsvSource({
            "'', PT0S",
            "' ', PT0S",
            "0, PT0S",
            "4, PT4M",
            "'4 ', PT4M",
            "' 4', PT4M",
            "14, PT14M",
            "140, PT140M",
            "3:00, PT3H",
            "3:0, PT3H",
            "2:34, PT2H34M",
            "2:3, PT2H3M",
    })
    void fromStringManuallyEntered(String input, Duration expectedDuration)
    {
        assertThat(converter().fromString(input)).isEqualTo(expectedDuration);
    }

    @ParameterizedTest
    @CsvSource(
    {
            "string",
            "1.2",
            "-00:01",
            "1:2:3",
    })
    void fromStringHandlesInvalidInput(String input)
    {
        assertThat(converter().fromString(input)).isNull();
    }

    private DurationStringConverter converter()
    {
        return new DurationStringConverter(new FormatterService(Locale.GERMAN, ZoneId.of("Europe/Berlin")));
    }
}
