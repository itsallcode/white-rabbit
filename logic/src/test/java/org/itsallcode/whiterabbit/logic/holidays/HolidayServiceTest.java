package org.itsallcode.whiterabbit.logic.holidays;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Hashtable;
import java.util.List;

import org.itsallcode.whiterabbit.logic.holidays.FloatingHoliday.Direction;
import org.itsallcode.whiterabbit.logic.holidays.parser.HolidaysFileParser;
import org.junit.jupiter.api.Test;

public class HolidayServiceTest
{
    @Test
    void illegalLine() throws IOException
    {
        final HolidaysFileParser parser = new HolidaysFileParser("illegal_input");
        parser.parse(new ByteArrayInputStream("#\n\nillegal line".getBytes()));
        assertThat(parser.getErrors().size()).isEqualTo(1);
        assertThat(parser.getErrors().get(0).lineNumber).isEqualTo(3);
    }

    @Test
    void allBarbarianHolidays() throws IOException
    {
        final Holiday[] expected = expectedBavarianHolidays();
        assertThat(readBavarianHolidays().getDefinitions()).containsExactlyInAnyOrder(expected);
    }

    @Test
    void bavarianHolidays_2021_04() throws IOException
    {
        final int year = 2021;
        final int month = 4;

        final Hashtable<Integer, String> expected = new Hashtable<>();
        expected.put(2, "Karfreitag");
        expected.put(4, "Ostersonntag");
        expected.put(5, "Ostermontag");

        assertHolidays(year, month, expected);
    }

    @Test
    void bavarianHolidays_2021_05() throws IOException
    {
        final int year = 2021;
        final int month = 5;

        final Hashtable<Integer, String> expected = new Hashtable<>();
        expected.put(1, "1. Mai");
        expected.put(13, "Christi Himmelfahrt");
        expected.put(23, "Pfingstsonntag");
        expected.put(24, "Pfingstmontag");

        assertHolidays(year, month, expected);
    }

    private void assertHolidays(final int year, final int month, final Hashtable<Integer, String> expected)
            throws IOException
    {
        final HolidayService service = readBavarianHolidays();
        final int n = LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        for (int i = 1; i <= n; i++)
        {
            final String expectedName = expected.get(i);
            final LocalDate date = LocalDate.of(year, month, i);
            if (expectedName == null)
            {
                assertThat(service.getHolidays(date)).isEmpty();
            }
            else
            {
                final List<Holiday> list = service.getHolidays(date);
                assertThat(list.size()).isEqualTo(1);
                final Holiday actual = list.get(0);
                assertThat(actual.getName()).isEqualTo(expectedName);
            }
        }
    }

    private HolidayService readBavarianHolidays() throws IOException
    {
        final HolidaysFileParser parser = new HolidaysFileParser("bavaria.txt");
        final List<Holiday> list = parser.parse(HolidayServiceTest.class.getResourceAsStream("bavaria.txt"));
        return new HolidayService(list);
    }

    private Holiday[] expectedBavarianHolidays()
    {
        return new Holiday[] {
                new FixedDateHoliday("holiday", "Neujahr", 1, 1),
                new FixedDateHoliday("holiday", "Heilige Drei Könige", 1, 6),
                new FixedDateHoliday("holiday", "1. Mai", 5, 1),
                new FixedDateHoliday("holiday", "Tag der Deutschen Einheit", 10, 3),

                new FloatingHoliday("holiday", "1. Advent", 4, DayOfWeek.SUNDAY, Direction.BEFORE, 12, 24),
                new FloatingHoliday("holiday", "2. Advent", 3, DayOfWeek.SUNDAY, Direction.BEFORE, 12, 24),
                new FloatingHoliday("holiday", "3. Advent", 2, DayOfWeek.SUNDAY, Direction.BEFORE, 12, 24),
                new FloatingHoliday("holiday", "4. Advent", 1, DayOfWeek.SUNDAY, Direction.BEFORE, 12, 24),
                new FixedDateHoliday("holiday", "1. Weihnachtstag", 12, 25),
                new FixedDateHoliday("holiday", "2. Weihnachtstag", 12, 26),

                new EasterBasedHoliday("holiday", "Rosenmontag", -48),
                new EasterBasedHoliday("holiday", "Karfreitag", -2),
                new EasterBasedHoliday("holiday", "Ostersonntag", 0),
                new EasterBasedHoliday("holiday", "Ostermontag", +1),
                new EasterBasedHoliday("holiday", "Christi Himmelfahrt", +39),
                new EasterBasedHoliday("holiday", "Pfingstsonntag", +49),
                new EasterBasedHoliday("holiday", "Pfingstmontag", +50),
                new EasterBasedHoliday("holiday", "Fronleichnam", +60),
                new FixedDateHoliday("holiday", "Mariae Himmelfahrt", 8, 15),
                new FixedDateHoliday("holiday", "Allerheiligen", 11, 1),
                new FloatingHoliday("holiday", "Totensonntag", 1, DayOfWeek.SUNDAY, Direction.AFTER, 11, 20) };
    }
}
