package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DayOfWeekWithoutDotFormatterTest
{
    private DayOfWeekWithoutDotFormatter formatter;

    @BeforeEach
    void setUp()
    {
        formatter = new DayOfWeekWithoutDotFormatter(null);
    }

    @Test
    public void formatLocalDateTime()
    {
        formatter = new DayOfWeekWithoutDotFormatter(
                DateTimeFormatter.ofPattern("EE dd.MM.yyyy 'CW'ww, HH:mm:ss", Locale.GERMAN));
        assertThat(formatter.format(LocalDateTime.of(2021, 05, 21, 5, 27))).isEqualTo("Fr 21.05.2021 CW21, 05:27:00");
    }

    @Test
    public void formatLocalDate()
    {
        formatter = new DayOfWeekWithoutDotFormatter(
                DateTimeFormatter.ofPattern("E dd.MM.", Locale.GERMAN));
        assertThat(formatter.format(LocalDate.of(2021, 05, 21))).isEqualTo("Fr 21.05.");
    }

    @Test
    public void nullString()
    {
        assertNull(formatter.dayOfWeekWithoutDot(null));
    }

    @Test
    public void shortString()
    {
        assertThat(formatter.dayOfWeekWithoutDot("ab")).isEqualTo("ab");
    }

    @Test
    public void withoutDot()
    {
        assertThat(formatter.dayOfWeekWithoutDot("abc")).isEqualTo("abc");
    }

    @Test
    public void withDot()
    {
        assertThat(formatter.dayOfWeekWithoutDot("ab.cde")).isEqualTo("abcde");
    }
}
