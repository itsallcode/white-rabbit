package org.itsallcode.whiterabbit.logic.holidays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DayOfWeek;

import org.itsallcode.whiterabbit.logic.holidays.parser.DayOfWeekParser;
import org.itsallcode.whiterabbit.logic.holidays.parser.HolidayParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HolidayParserTest
{
    private HolidayParser holidayParser;

    @BeforeEach
    private void setup()
    {
        holidayParser = new HolidayParser();
    }

    @Test
    void dayOfWeek()
    {
        // two letters
        assertThat(holidayParser.parse("holiday float -4 SU 12 24 1. Advent"))
                .isEqualTo(new FloatingHoliday("holiday", "1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
        // full
        assertThat(holidayParser.parse("holiday float -4 SUNDAY 12 24 1. Advent"))
                .isEqualTo(new FloatingHoliday("holiday", "1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
        // lowercase
        assertThat(holidayParser.parse("holiday float -4 sunday 12 24 1. Advent"))
                .isEqualTo(new FloatingHoliday("holiday", "1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
    }

    @Test
    void whitespace()
    {
        assertThat(holidayParser.parse("   holiday    float  -4 SUN\t 12 24 1. Advent     "))
                .isEqualTo(new FloatingHoliday("holiday", "1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
    }

    @Test
    void invalidPivotDate()
    {
        assertNull(holidayParser.parse("holiday float -4 SU 13 1 Famous Februar, 30th"));
        assertNull(holidayParser.parse("holiday float -4 SU 01 32 Famous Februar, 30th"));
    }

    @Test
    void illegalType()
    {
        assertNull(holidayParser.parse("holiday illegaType -4 SU 12 24 1. Advent"));
    }

    @Test
    void ambigueDayOfWeekAbbreviation()
    {
        assertThrows(DayOfWeekParser.AmbigueDayOfWeekAbbreviationException.class,
                () -> holidayParser.parse("holiday float 1 S 1 1 Ambigue January S-day"));
        assertThrows(DayOfWeekParser.AmbigueDayOfWeekAbbreviationException.class,
                () -> holidayParser.parse("holiday float 1 T 1 1 Ambigue January T-day"));
    }

    @Test
    void nonAmbigueDayOfWeekAbbreviation()
    {
        assertThat(holidayParser.parse("holiday float 1 M 1 1 Non-ambigue M-day"))
                .isEqualTo(new FloatingHoliday("holiday", "Non-ambigue M-day", 1, DayOfWeek.MONDAY, 1, 1));
        assertThat(holidayParser.parse("holiday float 1 W 1 1 Non-ambigue W-day"))
                .isEqualTo(new FloatingHoliday("holiday", "Non-ambigue W-day", 1, DayOfWeek.WEDNESDAY, 1, 1));
        assertThat(holidayParser.parse("holiday float 1 W 1 1 Non-ambigue F-day"))
                .isEqualTo(new FloatingHoliday("holiday", "Non-ambigue F-day", 1, DayOfWeek.WEDNESDAY, 1, 1));
    }

    @Test
    void offsetTooLarge()
    {
        assertNull(holidayParser.parse("holiday float -213 SU 12 24 1. Advent"));
    }

    @Test
    void notEqualFixed()
    {
        // day
        assertThat(holidayParser.parse("holiday fixed 1 1 Neujahr"))
                .isNotEqualTo(new FixedDateHoliday("holiday", "Neujahr", 1, 2));
        // name
        assertThat(holidayParser.parse("holiday fixed 1 1 Neujahr"))
                .isNotEqualTo(new FixedDateHoliday("holiday", "xxx", 1, 1));
    }

    @Test
    void notEqualFLoating()
    {
        // name
        assertThat(holidayParser.parse("holiday float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("holiday", "1. Advent AAA", -4, DayOfWeek.SUNDAY, 12, 24));
        // month
        assertThat(holidayParser.parse("holiday float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("holiday", "1. Advent", -4, DayOfWeek.SUNDAY, 11, 24));
        // day
        assertThat(holidayParser.parse("holiday float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("holiday", "1. Advent", -4, DayOfWeek.SUNDAY, 12, 23));
        // day of week
        assertThat(holidayParser.parse("holiday float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("holiday", "1. Advent", -4, DayOfWeek.MONDAY, 12, 24));
        // offset
        assertThat(holidayParser.parse("holiday float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("holiday", "1. Advent", -2, DayOfWeek.SUNDAY, 12, 24));
    }

    @Test
    void leadingZeros()
    {
        assertThat(holidayParser.parse("holiday fixed 01 01 Neujahr"))
                .isEqualTo(new FixedDateHoliday("holiday", "Neujahr", 1, 1));
        assertThat(holidayParser.parse("holiday float +01 MON 01 01 Fictional New Year's Monday"))
                .isEqualTo(new FloatingHoliday("holiday", "Fictional New Year's Monday", 1, DayOfWeek.MONDAY, 1, 1));
    }

    @Test
    void successfulParsing()
    {
        assertThat(holidayParser.parse("holiday fixed 1 1 Neujahr"))
                .isEqualTo(new FixedDateHoliday("holiday", "Neujahr", 1, 1));
        assertThat(holidayParser.parse("holiday fixed 1 6 Heilige Drei Könige"))
                .isEqualTo(new FixedDateHoliday("holiday", "Heilige Drei Könige", 1, 6));
        assertThat(holidayParser.parse("holiday fixed 5 1 1. Mai"))
                .isEqualTo(new FixedDateHoliday("holiday", "1. Mai", 5, 1));
        assertThat(holidayParser.parse("holiday fixed 10 3 Tag der Deutschen Einheit"))
                .isEqualTo(new FixedDateHoliday("holiday", "Tag der Deutschen Einheit", 10, 3));

        assertThat(holidayParser.parse("holiday float -4 SUN 12 24 1. Advent"))
                .isEqualTo(new FloatingHoliday("holiday", "1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
        assertThat(holidayParser.parse("holiday float -3 SUN 12 24 2. Advent"))
                .isEqualTo(new FloatingHoliday("holiday", "2. Advent", -3, DayOfWeek.SUNDAY, 12, 24));
        assertThat(holidayParser.parse("holiday float -2 SUN 12 24 3. Advent"))
                .isEqualTo(new FloatingHoliday("holiday", "3. Advent", -2, DayOfWeek.SUNDAY, 12, 24));
        assertThat(holidayParser.parse("holiday float -1 SUN 12 24 4. Advent"))
                .isEqualTo(new FloatingHoliday("holiday", "4. Advent", -1, DayOfWeek.SUNDAY, 12, 24));
        assertThat(holidayParser.parse("holiday fixed 12 25 1. Weihnachtstag"))
                .isEqualTo(new FixedDateHoliday("holiday", "1. Weihnachtstag", 12, 25));
        assertThat(holidayParser.parse("holiday fixed 12 26 2. Weihnachtstag"))
                .isEqualTo(new FixedDateHoliday("holiday", "2. Weihnachtstag", 12, 26));

        assertThat(holidayParser.parse("holiday easter -48 Rosenmontag"))
                .isEqualTo(new EasterBasedHoliday("holiday", "Rosenmontag", -48));
        assertThat(holidayParser.parse("holiday easter -2 Karfreitag"))
                .isEqualTo(new EasterBasedHoliday("holiday", "Karfreitag", -2));
        assertThat(holidayParser.parse("holiday easter 0 Ostersonntag"))
                .isEqualTo(new EasterBasedHoliday("holiday", "Ostersonntag", 0));
        assertThat(holidayParser.parse("holiday easter +1 Ostermontag"))
                .isEqualTo(new EasterBasedHoliday("holiday", "Ostermontag", +1));

        assertThat(holidayParser.parse("holiday easter +39 Christi Himmelfahrt"))
                .isEqualTo(new EasterBasedHoliday("holiday", "Christi Himmelfahrt", +39));
        assertThat(holidayParser.parse("holiday easter +49 Pfingstsonntag"))
                .isEqualTo(new EasterBasedHoliday("holiday", "Pfingstsonntag", +49));
        assertThat(holidayParser.parse("holiday easter +50 Pfingstmontag"))
                .isEqualTo(new EasterBasedHoliday("holiday", "Pfingstmontag", +50));
        assertThat(holidayParser.parse("holiday easter +60 Fronleichnam"))
                .isEqualTo(new EasterBasedHoliday("holiday", "Fronleichnam", +60));
        assertThat(holidayParser.parse("holiday fixed 8 15 Mariae Himmelfahrt"))
                .isEqualTo(new FixedDateHoliday("holiday", "Mariae Himmelfahrt", 8, 15));
        assertThat(holidayParser.parse("holiday fixed 11 1 Allerheiligen"))
                .isEqualTo(new FixedDateHoliday("holiday", "Allerheiligen", 11, 1));
        assertThat(holidayParser.parse("holiday float 1 SUN 11 20 Totensonntag"))
                .isEqualTo(new FloatingHoliday("holiday", "Totensonntag", 1, DayOfWeek.SUNDAY, 11, 20));
    }

}
