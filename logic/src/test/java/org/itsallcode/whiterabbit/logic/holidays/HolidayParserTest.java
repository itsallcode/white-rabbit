package org.itsallcode.whiterabbit.logic.holidays;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;

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
        assertThat(holidayParser.parse("float -4 SU 12 24 1. Advent"))
                .isEqualTo(new FloatingHoliday("1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
        // full
        assertThat(holidayParser.parse("float -4 SUNDAY 12 24 1. Advent"))
                .isEqualTo(new FloatingHoliday("1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
        // lowercase
        assertThat(holidayParser.parse("float -4 sunday 12 24 1. Advent"))
                .isEqualTo(new FloatingHoliday("1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
    }

    @Test
    void whitespace()
    {
        assertThat(holidayParser.parse("   float  -4 SUN\t 12 24 1. Advent     "))
                .isEqualTo(new FloatingHoliday("1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
    }

    @Test
    void notEqualFixed()
    {
        // day
        assertThat(holidayParser.parse("fixed 1 1 Neujahr"))
                .isNotEqualTo(new FixedDateHoliday("Neujahr", 1, 2));
        // name
        assertThat(holidayParser.parse("fixed 1 1 Neujahr"))
                .isNotEqualTo(new FixedDateHoliday("xxx", 1, 1));
    }

    @Test
    void notEqualFLoating()
    {
        // name
        assertThat(holidayParser.parse("float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("1. Advent AAA", -4, DayOfWeek.SUNDAY, 12, 24));
        // month
        assertThat(holidayParser.parse("float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("1. Advent", -4, DayOfWeek.SUNDAY, 11, 24));
        // day
        assertThat(holidayParser.parse("float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("1. Advent", -4, DayOfWeek.SUNDAY, 12, 23));
        // day of week
        assertThat(holidayParser.parse("float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("1. Advent", -4, DayOfWeek.MONDAY, 12, 24));
        // offset
        assertThat(holidayParser.parse("float -4 SUN 12 24 1. Advent"))
                .isNotEqualTo(new FloatingHoliday("1. Advent", -2, DayOfWeek.SUNDAY, 12, 24));
    }

    @Test
    void successfulParsing()
    {
        assertThat(holidayParser.parse("fixed 1 1 Neujahr"))
                .isEqualTo(new FixedDateHoliday("Neujahr", 1, 1));
        assertThat(holidayParser.parse("fixed 1 6 Heilige Drei Könige"))
                .isEqualTo(new FixedDateHoliday("Heilige Drei Könige", 1, 6));
        assertThat(holidayParser.parse("fixed 5 1 1. Mai"))
                .isEqualTo(new FixedDateHoliday("1. Mai", 5, 1));
        assertThat(holidayParser.parse("fixed 10 3 Tag der Deutschen Einheit"))
                .isEqualTo(new FixedDateHoliday("Tag der Deutschen Einheit", 10, 3));

        assertThat(holidayParser.parse("float -4 SUN 12 24 1. Advent"))
                .isEqualTo(new FloatingHoliday("1. Advent", -4, DayOfWeek.SUNDAY, 12, 24));
        assertThat(holidayParser.parse("float -3 SUN 12 24 2. Advent"))
                .isEqualTo(new FloatingHoliday("2. Advent", -3, DayOfWeek.SUNDAY, 12, 24));
        assertThat(holidayParser.parse("float -2 SUN 12 24 3. Advent"))
                .isEqualTo(new FloatingHoliday("3. Advent", -2, DayOfWeek.SUNDAY, 12, 24));
        assertThat(holidayParser.parse("float -1 SUN 12 24 4. Advent"))
                .isEqualTo(new FloatingHoliday("4. Advent", -1, DayOfWeek.SUNDAY, 12, 24));
        assertThat(holidayParser.parse("fixed 12 25 1. Weihnachtstag"))
                .isEqualTo(new FixedDateHoliday("1. Weihnachtstag", 12, 25));
        assertThat(holidayParser.parse("fixed 12 26 2. Weihnachtstag"))
                .isEqualTo(new FixedDateHoliday("2. Weihnachtstag", 12, 26));

        assertThat(holidayParser.parse("easter -48 Rosenmontag"))
                .isEqualTo(new EasterBasedHoliday("Rosenmontag", -48));
        assertThat(holidayParser.parse("easter -2 Karfreitag"))
                .isEqualTo(new EasterBasedHoliday("Karfreitag", -2));
        assertThat(holidayParser.parse("easter 0 Ostersonntag"))
                .isEqualTo(new EasterBasedHoliday("Ostersonntag", 0));
        assertThat(holidayParser.parse("easter +1 Ostermontag"))
                .isEqualTo(new EasterBasedHoliday("Ostermontag", +1));

        assertThat(holidayParser.parse("easter +39 Christi Himmelfahrt"))
                .isEqualTo(new EasterBasedHoliday("Christi Himmelfahrt", +39));
        assertThat(holidayParser.parse("easter +49 Pfingstsonntag"))
                .isEqualTo(new EasterBasedHoliday("Pfingstsonntag", +49));
        assertThat(holidayParser.parse("easter +50 Pfingstmontag"))
                .isEqualTo(new EasterBasedHoliday("Pfingstmontag", +50));
        assertThat(holidayParser.parse("easter +60 Fronleichnam"))
                .isEqualTo(new EasterBasedHoliday("Fronleichnam", +60));
        assertThat(holidayParser.parse("fixed 8 15 Mariae Himmelfahrt"))
                .isEqualTo(new FixedDateHoliday("Mariae Himmelfahrt", 8, 15));
        assertThat(holidayParser.parse("fixed 11 1 Allerheiligen"))
                .isEqualTo(new FixedDateHoliday("Allerheiligen", 11, 1));
        assertThat(holidayParser.parse("float 1 SUN 11 20 Totensonntag"))
                .isEqualTo(new FloatingHoliday("Totensonntag", 1, DayOfWeek.SUNDAY, 11, 20));
    }

}
