package org.itsallcode.whiterabbit.logic.report.vacation;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationMonth;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationYear;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VacationReportGeneratorTest
{
    private static final LocalDate DAY1 = LocalDate.of(2020, Month.JANUARY, 1);
    private static final LocalDate DAY2 = DAY1.plusDays(1);
    private static final LocalDate DAY3 = DAY1.plusDays(2);
    private static final LocalDate DAY4 = DAY1.plusDays(3);
    private static final LocalDate DAY5 = DAY1.plusDays(4);
    private static final LocalDate DAY6 = DAY1.plusDays(5);

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
        simulateMonths(month(2020, Month.JANUARY, DAY1));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(1);
        final VacationYear vacationYear = report.years.get(0);

        assertThat(vacationYear.getYear()).isEqualTo(Year.of(2020));
        assertThat(vacationYear.getDaysAvailable()).isEqualTo(3);
        assertThat(vacationYear.getDaysAvailable()).isEqualTo(3);
        assertThat(vacationYear.getDaysUsed()).isEqualTo(1);
        assertThat(vacationYear.getDaysRemaingFromPreviousYear()).isZero();
        assertThat(vacationYear.getDaysRemaining()).isEqualTo(2);
    }

    @Test
    void yearlyReportWithTwoMonthsSameYear()
    {
        simulateMonths(month(2020, Month.JANUARY, DAY1), month(2020, Month.FEBRUARY, DAY2, DAY3, DAY4));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(1);
        final VacationYear vacationYear = report.years.get(0);

        assertThat(vacationYear.getYear()).isEqualTo(Year.of(2020));
        assertThat(vacationYear.getDaysAvailable()).isEqualTo(5);
        assertThat(vacationYear.getDaysUsed()).isEqualTo(4);
        assertThat(vacationYear.getDaysRemaingFromPreviousYear()).isZero();
        assertThat(vacationYear.getDaysRemaining()).isEqualTo(1);
    }

    @Test
    void yearlyReportWithTwoYears()
    {
        simulateMonths(month(2019, Month.DECEMBER, DAY1), month(2020, Month.JANUARY, DAY2, DAY3, DAY4));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(2);
        final VacationYear firstYear = report.years.get(0);
        final VacationYear secondYear = report.years.get(1);

        assertThat(firstYear.getYear()).isEqualTo(Year.of(2019));
        assertThat(firstYear.getDaysAvailable()).isEqualTo(3);
        assertThat(firstYear.getDaysUsed()).isEqualTo(1);
        assertThat(firstYear.getDaysRemaingFromPreviousYear()).isZero();
        assertThat(firstYear.getDaysRemaining()).isEqualTo(2);

        assertThat(secondYear.getYear()).isEqualTo(Year.of(2020));
        assertThat(secondYear.getDaysAvailable()).isEqualTo(30);
        assertThat(secondYear.getDaysUsed()).isEqualTo(3);
        assertThat(secondYear.getDaysRemaingFromPreviousYear()).isEqualTo(2);
        assertThat(secondYear.getDaysRemaining()).isEqualTo(29);
    }

    @Test
    void yearlyReportWithTwoYearsMultipleMonths()
    {
        simulateMonths(month(2019, Month.DECEMBER, DAY1),
                month(2020, Month.JANUARY, DAY2, DAY3, DAY4),
                month(2020, Month.FEBRUARY),
                month(2020, Month.MARCH, DAY5, DAY6));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(2);
        final VacationYear firstYear = report.years.get(0);
        final VacationYear secondYear = report.years.get(1);

        assertThat(firstYear.getYear()).isEqualTo(Year.of(2019));
        assertThat(firstYear.getDaysAvailable()).isEqualTo(3);
        assertThat(firstYear.getDaysUsed()).isEqualTo(1);
        assertThat(firstYear.getDaysRemaingFromPreviousYear()).isZero();
        assertThat(firstYear.getDaysRemaining()).isEqualTo(2);

        assertThat(secondYear.getYear()).isEqualTo(Year.of(2020));
        assertThat(secondYear.getDaysAvailable()).isEqualTo(30);
        assertThat(secondYear.getDaysUsed()).isEqualTo(5);
        assertThat(secondYear.getDaysRemaingFromPreviousYear()).isEqualTo(2);
        assertThat(secondYear.getDaysRemaining()).isEqualTo(27);
    }

    @Test
    void yearlyReportWithNegativeVacation()
    {

        final LocalDate[] thirtyThreeDays = new LocalDate[33];
        Arrays.fill(thirtyThreeDays, DAY2);
        simulateMonths(month(2019, Month.DECEMBER, DAY1),
                month(2020, Month.JANUARY, thirtyThreeDays));
        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(2);
        final VacationYear firstYear = report.years.get(0);
        final VacationYear secondYear = report.years.get(1);

        assertThat(firstYear.getYear()).isEqualTo(Year.of(2019));
        assertThat(firstYear.getDaysAvailable()).isEqualTo(3);
        assertThat(firstYear.getDaysUsed()).isEqualTo(1);
        assertThat(firstYear.getDaysRemaingFromPreviousYear()).isZero();
        assertThat(firstYear.getDaysRemaining()).isEqualTo(2);

        assertThat(secondYear.getYear()).isEqualTo(Year.of(2020));
        assertThat(secondYear.getDaysAvailable()).isEqualTo(30);
        assertThat(secondYear.getDaysUsed()).isEqualTo(33);
        assertThat(secondYear.getDaysRemaingFromPreviousYear()).isEqualTo(2);
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
        simulateMonths(month(2020, Month.JANUARY));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.months).hasSize(1);
        final VacationMonth firstMonth = report.months.get(0);
        assertThat(firstMonth.getYearMonth()).isEqualTo(YearMonth.of(2020, Month.JANUARY));
        assertThat(firstMonth.getUsedVacationDayCount()).isZero();
        assertThat(firstMonth.getVacationDaysUsed()).isEmpty();
    }

    @Test
    void monthlyReportSingleMonthWithVacation()
    {
        simulateMonths(month(2020, Month.JANUARY, DAY1, DAY2, DAY3, DAY4));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.months).hasSize(1);
        final VacationMonth firstMonth = report.months.get(0);
        assertThat(firstMonth.getYearMonth()).isEqualTo(YearMonth.of(2020, Month.JANUARY));
        assertThat(firstMonth.getUsedVacationDayCount()).isEqualTo(4);
        assertThat(firstMonth.getVacationDaysUsed()).containsExactly(DAY1, DAY2, DAY3, DAY4);
    }

    @Test
    void monthlyReportTwoMonths()
    {
        simulateMonths(month(2020, Month.JANUARY, DAY1, DAY2, DAY3, DAY4),
                month(2020, Month.FEBRUARY, DAY5));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.months).hasSize(2);
        final VacationMonth firstMonth = report.months.get(0);
        assertThat(firstMonth.getYearMonth()).isEqualTo(YearMonth.of(2020, Month.JANUARY));
        assertThat(firstMonth.getUsedVacationDayCount()).isEqualTo(4);

        final VacationMonth secondMonth = report.months.get(1);
        assertThat(secondMonth.getYearMonth()).isEqualTo(YearMonth.of(2020, Month.FEBRUARY));
        assertThat(secondMonth.getUsedVacationDayCount()).isEqualTo(1);
    }

    @Test
    void monthlyReportTwoMonthsInDifferentYears()
    {
        simulateMonths(month(2019, Month.DECEMBER, DAY1, DAY2, DAY3, DAY4),
                month(2020, Month.JANUARY, DAY5));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.months).hasSize(2);
        final VacationMonth firstMonth = report.months.get(0);
        assertThat(firstMonth.getYearMonth()).isEqualTo(YearMonth.of(2019, Month.DECEMBER));
        assertThat(firstMonth.getUsedVacationDayCount()).isEqualTo(4);

        final VacationMonth secondMonth = report.months.get(1);
        assertThat(secondMonth.getYearMonth()).isEqualTo(YearMonth.of(2020, Month.JANUARY));
        assertThat(secondMonth.getUsedVacationDayCount()).isEqualTo(1);
    }

    private MonthIndex month(int year, Month month, LocalDate... vacationDays)
    {
        final MonthIndex monthData = mock(MonthIndex.class);
        when(monthData.getYearMonth()).thenReturn(YearMonth.of(year, month));
        when(monthData.getVacationDays()).thenReturn(asList(vacationDays));
        when(monthData.getVacationDayCount()).thenReturn(vacationDays.length);
        return monthData;
    }

    private void simulateMonths(MonthIndex... months)
    {
        final List<YearMonth> availableDataYearMonth = Arrays.stream(months).map(MonthIndex::getYearMonth)
                .toList();
        when(storageMock.getAvailableDataMonths()).thenReturn(availableDataYearMonth);

        for (final MonthIndex monthIndex : months)
        {
            when(storageMock.loadMonth(monthIndex.getYearMonth())).thenReturn(Optional.of(monthIndex));
        }
    }
}
