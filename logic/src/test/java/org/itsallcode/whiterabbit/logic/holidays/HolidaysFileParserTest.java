package org.itsallcode.whiterabbit.logic.holidays;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.util.List;

import org.itsallcode.whiterabbit.logic.holidays.parser.HolidaysFileParser;
import org.junit.jupiter.api.Test;

public class HolidaysFileParserTest
{
    @Test
    void testBavaria() throws IOException
    {
        final Holiday[] expected = createExpected();

        final InputStream stream = HolidaysFileParserTest.class.getResourceAsStream("bavaria.txt");
        final List<Holiday> actual = HolidaysFileParser.parse(stream);

        assertThat(actual).containsExactlyInAnyOrder(expected);
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
