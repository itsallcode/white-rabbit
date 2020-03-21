package org.itsallcode.whiterabbit.logic.service.vacation;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReport.VacationMonth;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReport.VacationYear;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VacationReportGeneratorTest
{
    @Mock
    private Storage storageMock;
    private VacationReportGenerator vacationService;

    @BeforeEach
    void setUp()
    {
        vacationService = new VacationReportGenerator(storageMock);
    }

    @Test
    void emptyYearlyReport()
    {
        simulateMonths();

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).isEmpty();
    }

    @Test
    void yearlyReportWithOneMonth()
    {
        simulateMonths(month(2020, Month.JANUARY, 1));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(1);
        final VacationYear vacationYear = report.years.get(0);

        assertThat(vacationYear.year).isEqualTo(Year.of(2020));
        assertThat(vacationYear.daysAvailable).isEqualTo(3);
        assertThat(vacationYear.daysUsed).isEqualTo(1);
        assertThat(vacationYear.daysRemaingFromLastYear).isEqualTo(0);
        assertThat(vacationYear.getDaysRemaining()).isEqualTo(2);
    }

    @Test
    void yearlyReportWithTwoMonthsSameYear()
    {
        simulateMonths(month(2020, Month.JANUARY, 1), month(2020, Month.FEBRUARY, 3));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(1);
        final VacationYear vacationYear = report.years.get(0);

        assertThat(vacationYear.year).isEqualTo(Year.of(2020));
        assertThat(vacationYear.daysAvailable).isEqualTo(5);
        assertThat(vacationYear.daysUsed).isEqualTo(4);
        assertThat(vacationYear.daysRemaingFromLastYear).isEqualTo(0);
        assertThat(vacationYear.getDaysRemaining()).isEqualTo(1);
    }

    @Test
    void yearlyReportWithTwoYears()
    {
        simulateMonths(month(2019, Month.DECEMBER, 1), month(2020, Month.JANUARY, 3));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(2);
        final VacationYear firstYear = report.years.get(0);
        final VacationYear secondYear = report.years.get(1);

        assertThat(firstYear.year).isEqualTo(Year.of(2019));
        assertThat(firstYear.daysAvailable).isEqualTo(3);
        assertThat(firstYear.daysUsed).isEqualTo(1);
        assertThat(firstYear.daysRemaingFromLastYear).isEqualTo(0);
        assertThat(firstYear.getDaysRemaining()).isEqualTo(2);

        assertThat(secondYear.year).isEqualTo(Year.of(2020));
        assertThat(secondYear.daysAvailable).isEqualTo(30);
        assertThat(secondYear.daysUsed).isEqualTo(3);
        assertThat(secondYear.daysRemaingFromLastYear).isEqualTo(2);
        assertThat(secondYear.getDaysRemaining()).isEqualTo(29);
    }

    @Test
    void yearlyReportWithTwoYearsMultipleMonths()
    {
        simulateMonths(month(2019, Month.DECEMBER, 1), month(2020, Month.JANUARY, 3), month(2020, Month.FEBRUARY, 0),
                month(2020, Month.MARCH, 15));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(2);
        final VacationYear firstYear = report.years.get(0);
        final VacationYear secondYear = report.years.get(1);

        assertThat(firstYear.year).isEqualTo(Year.of(2019));
        assertThat(firstYear.daysAvailable).isEqualTo(3);
        assertThat(firstYear.daysUsed).isEqualTo(1);
        assertThat(firstYear.daysRemaingFromLastYear).isEqualTo(0);
        assertThat(firstYear.getDaysRemaining()).isEqualTo(2);

        assertThat(secondYear.year).isEqualTo(Year.of(2020));
        assertThat(secondYear.daysAvailable).isEqualTo(30);
        assertThat(secondYear.daysUsed).isEqualTo(18);
        assertThat(secondYear.daysRemaingFromLastYear).isEqualTo(2);
        assertThat(secondYear.getDaysRemaining()).isEqualTo(14);
    }

    @Test
    void yearlyReportWithNegativeVacation()
    {
        simulateMonths(month(2019, Month.DECEMBER, 1), month(2020, Month.JANUARY, 33));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(2);
        final VacationYear firstYear = report.years.get(0);
        final VacationYear secondYear = report.years.get(1);

        assertThat(firstYear.year).isEqualTo(Year.of(2019));
        assertThat(firstYear.daysAvailable).isEqualTo(3);
        assertThat(firstYear.daysUsed).isEqualTo(1);
        assertThat(firstYear.daysRemaingFromLastYear).isEqualTo(0);
        assertThat(firstYear.getDaysRemaining()).isEqualTo(2);

        assertThat(secondYear.year).isEqualTo(Year.of(2020));
        assertThat(secondYear.daysAvailable).isEqualTo(30);
        assertThat(secondYear.daysUsed).isEqualTo(33);
        assertThat(secondYear.daysRemaingFromLastYear).isEqualTo(2);
        assertThat(secondYear.getDaysRemaining()).isEqualTo(-1);
    }

    @Test
    void monthlyReportEmpty()
    {
        simulateMonths();

        final VacationReport report = vacationService.generateReport();

        assertThat(report.months).isEmpty();
    }

    @Test
    void monthlyReportSingleMonthNoVacation()
    {
        simulateMonths(month(2020, Month.JANUARY, 0));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.months).hasSize(1);
        final VacationMonth firstMonth = report.months.get(0);
        assertThat(firstMonth.yearMonth).isEqualTo(YearMonth.of(2020, Month.JANUARY));
        assertThat(firstMonth.vacationDaysUsed).isEqualTo(0);
    }

    @Test
    void monthlyReportSingleMonthWithVacation()
    {
        simulateMonths(month(2020, Month.JANUARY, 4));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.months).hasSize(1);
        final VacationMonth firstMonth = report.months.get(0);
        assertThat(firstMonth.yearMonth).isEqualTo(YearMonth.of(2020, Month.JANUARY));
        assertThat(firstMonth.vacationDaysUsed).isEqualTo(4);
    }

    @Test
    void monthlyReportTwoMonths()
    {
        simulateMonths(month(2020, Month.JANUARY, 4), month(2020, Month.FEBRUARY, 1));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.months).hasSize(2);
        final VacationMonth firstMonth = report.months.get(0);
        assertThat(firstMonth.yearMonth).isEqualTo(YearMonth.of(2020, Month.JANUARY));
        assertThat(firstMonth.vacationDaysUsed).isEqualTo(4);

        final VacationMonth secondMonth = report.months.get(1);
        assertThat(secondMonth.yearMonth).isEqualTo(YearMonth.of(2020, Month.FEBRUARY));
        assertThat(secondMonth.vacationDaysUsed).isEqualTo(1);
    }

    @Test
    void monthlyReportTwoMonthsInDifferentYears()
    {
        simulateMonths(month(2019, Month.DECEMBER, 4), month(2020, Month.JANUARY, 1));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.months).hasSize(2);
        final VacationMonth firstMonth = report.months.get(0);
        assertThat(firstMonth.yearMonth).isEqualTo(YearMonth.of(2019, Month.DECEMBER));
        assertThat(firstMonth.vacationDaysUsed).isEqualTo(4);

        final VacationMonth secondMonth = report.months.get(1);
        assertThat(secondMonth.yearMonth).isEqualTo(YearMonth.of(2020, Month.JANUARY));
        assertThat(secondMonth.vacationDaysUsed).isEqualTo(1);
    }

    private MonthIndex month(int year, Month month, int vacation)
    {
        final MonthIndex monthData = mock(MonthIndex.class);
        when(monthData.getYearMonth()).thenReturn(YearMonth.of(year, month));
        when(monthData.getVacationDayCount()).thenReturn(vacation);
        return monthData;
    }

    private void simulateMonths(MonthIndex... months)
    {
        final List<YearMonth> availableDataYearMonth = Arrays.stream(months).map(MonthIndex::getYearMonth)
                .collect(toList());
        when(storageMock.getAvailableDataYearMonth()).thenReturn(availableDataYearMonth);

        for (final MonthIndex monthIndex : months)
        {
            when(storageMock.loadMonth(monthIndex.getYearMonth())).thenReturn(Optional.of(monthIndex));
        }
    }
}
