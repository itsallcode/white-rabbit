package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.itsallcode.whiterabbit.api.features.Holidays.HolidayInstance;
import org.junit.jupiter.api.Test;

public class CalculatedHolidaysTest
{
    static final LocalDate DATE = LocalDate.of(2021, 06, 3);

    @Test
    void test()
    {
        final String filename = CalculatedHolidays.HOLIDAYS_CONFIGURATION_FILE;
        final CalculatedHolidays h = new CalculatedHolidays(
                null, filename, this.getClass().getResourceAsStream(filename));

        assertThat(h.getHolidays(DATE).size()).isEqualTo(1);
        final HolidayInstance actual = h.getHolidays(DATE).get(0);
        assertThat(actual.getName()).isEqualTo("Fronleichnam");
        assertThat(actual.getDate()).isEqualTo(DATE);
    }
}
