package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.itsallcode.whiterabbit.api.features.Holidays.HolidayInstance;
import org.junit.jupiter.api.Test;

class CalculatedHolidaysTest
{
    static final LocalDate DATE = LocalDate.of(2021, 06, 3);

    @Test
    void test() throws URISyntaxException
    {
        final String filename = CalculatedHolidays.HOLIDAYS_CONFIGURATION_FILE;
        final Path path = Paths.get(this.getClass().getResource(filename).toURI()).getParent();

        final CalculatedHolidays h = new CalculatedHolidays(path);

        assertThat(h.getHolidays(DATE).size()).isEqualTo(1);
        final HolidayInstance actual = h.getHolidays(DATE).get(0);
        assertThat(actual.getName()).isEqualTo("Fronleichnam");
        assertThat(actual.getDate()).isEqualTo(DATE);
    }
}
