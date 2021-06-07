package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.itsallcode.holidays.calculator.logic.Holiday;
import org.itsallcode.whiterabbit.api.features.Holidays.HolidayInstance;
import org.junit.jupiter.api.Test;

class CalculatedHolidaysTest
{
    static final LocalDate DATE = LocalDate.of(2021, 06, 3);

    @Test
    void success() throws URISyntaxException
    {
        final Path path = Paths.get(this.getClass().getResource("holidays.cfg").toURI()).getParent();
        final CalculatedHolidays holidays = new CalculatedHolidays(path);

        assertThat(holidays.getHolidays(DATE).size()).isEqualTo(1);
        final HolidayInstance actual = holidays.getHolidays(DATE).get(0);
        assertThat(actual.getName()).isEqualTo("Fronleichnam");
        assertThat(actual.getDate()).isEqualTo(DATE);
    }

    @Test
    void nonExistingConfigurationFile() throws URISyntaxException
    {
        final Path path = Paths.get(this.getClass().getResource("/").toURI());
        assertThat(new CalculatedHolidaysMock(path).readHolidays(path.resolve("non-existing-file.cfg"))).asList()
                .isEmpty();
    }

    private static class CalculatedHolidaysMock extends CalculatedHolidays
    {
        public CalculatedHolidaysMock(Path dataDir)
        {
            super(dataDir);
        }

        @Override
        public List<Holiday> readHolidays(Path configurationFile)
        {
            return super.readHolidays(configurationFile);
        }

    }
}
