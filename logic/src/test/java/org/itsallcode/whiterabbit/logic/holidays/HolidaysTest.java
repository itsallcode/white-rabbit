package org.itsallcode.whiterabbit.logic.holidays;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.itsallcode.whiterabbit.logic.holidays.parser.HolidaysFileParser;
import org.junit.jupiter.api.Test;

public class HolidaysTest
{

    @Test
    void testInstances() throws IOException
    {
        final int year = 2021;
        final int month = 4;

        final Holiday[] expected = new Holiday[] {
                new EasterBasedHoliday("Karfreitag", -2),
                new EasterBasedHoliday("Ostersonntag", 0),
                new EasterBasedHoliday("Ostermontag", +1)
        };
        final HolidayInstance[] expectedInstances = Arrays.asList(expected)
                .stream()
                .map(h -> h.getInstance(year))
                .collect(toList())
                .toArray(new HolidayInstance[0]);

        assertThat(readBavarianHolidays().getInstances(year, month)).containsExactly(expectedInstances);
    }

    @Test
    void testDayNumbers() throws IOException
    {
        final int year = 2021;
        final int month = 5;

        final Set<Integer> expected = new HashSet<>();
        expected.addAll(Arrays.asList(new Integer[] { 1, 13, 23, 24 }));

        assertThat(readBavarianHolidays().getDayNumbers(year, month)).isEqualTo(expected);
    }

    @Test
    void testBavaria() throws IOException
    {
        final Holiday[] expected = createExpected();
        assertThat(readBavarianHolidays().getDefinitions()).containsExactlyInAnyOrder(expected);
    }

    @Test
    void testIllegalLine() throws IOException
    {
        final HolidaysFileParser parser = new HolidaysFileParser("illegal_input");
        parser.parse(new ByteArrayInputStream("#\n\nillegal line".getBytes()));
        assertThat(parser.getErrors().size()).isEqualTo(1);
        assertThat(parser.getErrors().get(0).lineNumber).isEqualTo(3);
    }

    private Holidays readBavarianHolidays() throws IOException
    {
        final HolidaysFileParser parser = new HolidaysFileParser("bavaria.txt");
        return parser.parse(HolidaysTest.class.getResourceAsStream("bavaria.txt"));
    }

    private Holiday[] createExpected()
    {
        return new Holiday[] {
                new FixedDateHoliday("Neujahr", 1, 1),
                new FixedDateHoliday("Heilige Drei Könige", 1, 6),
                new FixedDateHoliday("1. Mai", 5, 1),
                new FixedDateHoliday("Tag der Deutschen Einheit", 10, 3),

                new FloatingHoliday("1. Advent", -4, DayOfWeek.SUNDAY, 12, 24),
                new FloatingHoliday("2. Advent", -3, DayOfWeek.SUNDAY, 12, 24),
                new FloatingHoliday("3. Advent", -2, DayOfWeek.SUNDAY, 12, 24),
                new FloatingHoliday("4. Advent", -1, DayOfWeek.SUNDAY, 12, 24),
                new FixedDateHoliday("1. Weihnachtstag", 12, 25),
                new FixedDateHoliday("2. Weihnachtstag", 12, 26),

                new EasterBasedHoliday("Rosenmontag", -48),
                new EasterBasedHoliday("Karfreitag", -2),
                new EasterBasedHoliday("Ostersonntag", 0),
                new EasterBasedHoliday("Ostermontag", +1),
                new EasterBasedHoliday("Christi Himmelfahrt", +39),
                new EasterBasedHoliday("Pfingstsonntag", +49),
                new EasterBasedHoliday("Pfingstmontag", +50),
                new EasterBasedHoliday("Fronleichnam", +60),
                new FixedDateHoliday("Mariae Himmelfahrt", 8, 15),
                new FixedDateHoliday("Allerheiligen", 11, 1),
                new FloatingHoliday("Totensonntag", 1, DayOfWeek.SUNDAY, 11, 20) };
    }

}
