package org.itsallcode.whiterabbit.logic.service.vacation;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReport.VacationYear;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VacationServiceTest
{
    @Mock
    private Storage storageMock;
    private VacationService vacationService;

    @BeforeEach
    void setUp()
    {
        vacationService = new VacationService(storageMock);
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
        simulateMonths(month(2020, 1, 1));

        final VacationReport report = vacationService.generateReport();

        assertThat(report.years).hasSize(1);
        final VacationYear vacationYear = report.years.get(0);

        assertThat(vacationYear.year).isEqualTo(Year.of(2020));
        assertThat(vacationYear.daysAvailable).isEqualTo(3);
        assertThat(vacationYear.daysUsed).isEqualTo(1);
        assertThat(vacationYear.daysRemaingFromLastYear).isEqualTo(0);
    }

    private MonthIndex month(int year, int month, Integer vacation)
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
            when(storageMock.loadMonth(monthIndex.getYearMonth())).thenReturn(monthIndex);
        }
    }
}
