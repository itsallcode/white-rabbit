package org.itsallcode.whiterabbit.jfxui.table.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CustomLocalTimeStringConverterTest
{
    private CustomLocalTimeStringConverter converter;

    @BeforeEach
    void setUp()
    {
        converter = new CustomLocalTimeStringConverter(Locale.GERMAN);
    }

    @Test
    void toStringFormatsLocalTime()
    {
        assertThat(converter.toString(LocalTime.of(13, 37))).isEqualTo("13:37");
    }

    @ParameterizedTest(name = "Parsing ''{0}'' returns {1}")
    @CsvSource(value =
    {
            "'', NULL",
            "0:00, 00:00",
            "1, 01:00",
            "9, 09:00",
            "09, 09:00",
            "12, 12:00",
            "13, 13:00",
            "0:01, 00:01",
            "1:00, 01:00",
            "1:23, 01:23",
            "1:23, 01:23",
            "01:23, 01:23",
            "123, 01:23",
            "0123, 01:23",
            "0000, 00:00",
            "23:59, 23:59",
            "2359, 23:59",
    }, nullValues = "NULL")
    void fromStringParsesLocalTime(String input, LocalTime expectedResult)
    {
        assertThat(converter.fromString(input)).isEqualTo(expectedResult);
    }

    @ParameterizedTest(name = "Parsing '{0}' returns {1}")
    @CsvSource(value =
    {
            "a",
            "00000",
            "12345" })
    void fromStringFailsParsing(String input)
    {
        assertThatThrownBy(() -> converter.fromString(input)).isInstanceOf(DateTimeParseException.class)
                .hasMessageContaining("Text '" + input + "' could not be parsed at index");
    }
}
