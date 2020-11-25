package org.itsallcode.whiterabbit.logic.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageTest
{
    private static final YearMonth YEAR_MONTH = YearMonth.of(2020, Month.NOVEMBER);
    private static final YearMonth PREVIOUS_MONTH = YearMonth.of(2020, Month.OCTOBER);
    @Mock
    ContractTermsService contractTermsMock;
    @Mock
    ProjectService projectServiceMock;
    @Mock
    JsonFileStorage fileStorageMock;

    Storage storage;

    @BeforeEach
    void setUp()
    {
        storage = new Storage(contractTermsMock, projectServiceMock, fileStorageMock);
    }

    @Test
    void loadPreviousMonthOvertime_returnsZero_whenNoPreviousMonth()
    {
        when(fileStorageMock.loadMonthRecord(YearMonth.of(2020, Month.OCTOBER))).thenReturn(Optional.empty());

        assertThat(storage.loadPreviousMonthOvertime(YEAR_MONTH)).isZero();
    }

    @Test
    void loadPreviousMonthOvertime_returnsPreviousMonthOvertime()
    {
        when(fileStorageMock.loadMonthRecord(PREVIOUS_MONTH))
                .thenReturn(Optional.of(jsonMonth(PREVIOUS_MONTH, Duration.ofMinutes(5))));

        assertThat(storage.loadPreviousMonthOvertime(YEAR_MONTH)).hasMinutes(5);
    }

    @Test
    void loadPreviousMonthOvertime_truncatesToMinute()
    {
        when(fileStorageMock.loadMonthRecord(PREVIOUS_MONTH))
                .thenReturn(Optional.of(jsonMonth(PREVIOUS_MONTH, Duration.ofMinutes(5).plusSeconds(50))));

        assertThat(storage.loadPreviousMonthOvertime(YEAR_MONTH)).hasMinutes(5);
    }

    @Test
    void loadOrCreate_createsNewMonthIfNotExists()
    {
        when(fileStorageMock.loadMonthRecord(YEAR_MONTH)).thenReturn(Optional.empty());

        final MonthIndex newMonth = storage.loadOrCreate(YEAR_MONTH);

        assertThat(newMonth.getYearMonth()).isEqualTo(YEAR_MONTH);
        assertThat(newMonth.getSortedDays()).hasSize(30);
        assertThat(newMonth.getTotalOvertime()).isZero();
        assertThat(newMonth.getVacationDayCount()).isZero();
    }

    @Test
    void loadOrCreate_loadsExistingMonth()
    {
        final JsonMonth month = jsonMonth(YEAR_MONTH, Duration.ofMinutes(4));
        when(fileStorageMock.loadMonthRecord(YEAR_MONTH)).thenReturn(Optional.of(month));

        final MonthIndex newMonth = storage.loadOrCreate(YEAR_MONTH);

        assertThat(newMonth.getYearMonth()).isEqualTo(YEAR_MONTH);
        assertThat(newMonth.getSortedDays()).hasSize(30);
        assertThat(newMonth.getTotalOvertime()).hasMinutes(4);
        assertThat(newMonth.getVacationDayCount()).isZero();
        assertThat(newMonth.getOvertimePreviousMonth()).hasMinutes(4);
    }

    private JsonMonth jsonMonth(YearMonth yearMonth, Duration overtimePreviousMonth)
    {
        final JsonMonth month = new JsonMonth();
        month.setYear(yearMonth.getYear());
        month.setMonth(yearMonth.getMonth());
        month.setOvertimePreviousMonth(overtimePreviousMonth);
        month.setDays(new ArrayList<>());
        return month;
    }
}
