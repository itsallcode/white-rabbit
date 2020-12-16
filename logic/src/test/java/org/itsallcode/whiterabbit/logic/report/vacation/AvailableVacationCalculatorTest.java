package org.itsallcode.whiterabbit.logic.report.vacation;

import static org.assertj.core.api.Assertions.assertThat;

import org.itsallcode.whiterabbit.logic.report.vacation.AvailableVacationCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AvailableVacationCalculatorTest
{
    @DisplayName("Vacation days in first year")
    @ParameterizedTest(name = "{0} working month should mean {1} vacation days")
    @CsvSource({ "0,0", "1,3", "2,5", "3,8", "4,10", "5,13", "6,15", "7,18", "8,20", "9,23", "10,25", "11,28",
            "12,30" })
    void vacationDaysFirstYear(int workingMonths, int expectedVacationDays)
    {
        assertThat(new AvailableVacationCalculator().getVacationDaysFirstYear(workingMonths)).as("vacation days")
                .isEqualTo(expectedVacationDays);
    }

    @Test
    void vacationDaysPerYear()
    {
        assertThat(new AvailableVacationCalculator().getVacationDaysPerYear()).isEqualTo(30);
    }
}
