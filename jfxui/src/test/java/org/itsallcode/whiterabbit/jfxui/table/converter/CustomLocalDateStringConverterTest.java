package org.itsallcode.whiterabbit.jfxui.table.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.table.days.CustomLocalDateStringConverter;
import org.itsallcode.whiterabbit.logic.service.DayOfWeekWithoutDotFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomLocalDateStringConverterTest
{
    private CustomLocalDateStringConverter converter;

    @BeforeEach
    void setUp()
    {
        converter = new CustomLocalDateStringConverter(
                new DayOfWeekWithoutDotFormatter(DateTimeFormatter.ofPattern("E dd.MM.", Locale.GERMAN)));
    }

    @Test
    void testToString()
    {
        assertThat(converter.toString(LocalDate.of(2021, 02, 22))).isEqualTo("Mo 22.02.");
    }
}
