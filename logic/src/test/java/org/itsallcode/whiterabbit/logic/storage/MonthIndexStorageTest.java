package org.itsallcode.whiterabbit.logic.storage;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.api.features.MonthDataStorage;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.holidays.HolidayService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.storage.data.JsonModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MonthIndexStorageTest
{
    private static final YearMonth YEAR_MONTH = YearMonth.of(2020, Month.NOVEMBER);
    private static final YearMonth PREVIOUS_MONTH = YearMonth.of(2020, Month.OCTOBER);

    @Mock
    ContractTermsService contractTermsMock;
    @Mock
    ProjectService projectServiceMock;
    @Mock
    MonthDataStorage fileStorageMock;
    @Mock
    MonthIndex monthIndexMock;
    @Mock
    HolidayService holidayService;

    private MonthIndexStorage storage;

    @BeforeEach
    void setUp()
    {
        storage = new MonthIndexStorage(contractTermsMock, projectServiceMock, fileStorageMock, holidayService);
        lenient().when(fileStorageMock.getModelFactory()).thenReturn(new JsonModelFactory());
    }

    @Test
    void loadPreviousMonthOvertime_returnsZero_whenNoPreviousMonth()
    {
        when(fileStorageMock.load(YearMonth.of(2020, Month.OCTOBER))).thenReturn(Optional.empty());

        assertThat(storage.loadPreviousMonthOvertime(YEAR_MONTH)).isZero();
    }

    @Test
    void loadPreviousMonthOvertime_returnsPreviousMonthOvertime()
    {
        when(fileStorageMock.load(PREVIOUS_MONTH))
                .thenReturn(Optional.of(jsonMonth(PREVIOUS_MONTH, Duration.ofMinutes(5))));

        assertThat(storage.loadPreviousMonthOvertime(YEAR_MONTH)).hasMinutes(5);
    }

    @Test
    void loadPreviousMonthOvertime_truncatesToMinute()
    {
        when(fileStorageMock.load(PREVIOUS_MONTH))
                .thenReturn(Optional.of(jsonMonth(PREVIOUS_MONTH, Duration.ofMinutes(5).plusSeconds(50))));

        assertThat(storage.loadPreviousMonthOvertime(YEAR_MONTH)).hasMinutes(5);
    }

    @Test
    void loadOrCreate_createsNewMonthIfNotExists()
    {
        when(fileStorageMock.load(YEAR_MONTH)).thenReturn(Optional.empty());

        final LocalDate sampleDay = YEAR_MONTH.atDay(3);

        final DayData dayData = fileStorageMock.getModelFactory().createDayData();
        dayData.setType(DayType.HOLIDAY);
        dayData.setDate(sampleDay);
        final List<DayData> holidays = new ArrayList<DayData>();
        holidays.add(dayData);

        when(holidayService.getHolidays(fileStorageMock.getModelFactory(), YEAR_MONTH)).thenReturn(holidays);

        final MonthIndex newMonth = storage.loadOrCreate(YEAR_MONTH);

        assertThat(newMonth.getYearMonth()).isEqualTo(YEAR_MONTH);
        assertThat(newMonth.getSortedDays()).hasSize(30);
        assertThat(newMonth.getDay(sampleDay).getType()).isEqualTo(DayType.HOLIDAY);
        assertThat(newMonth.getTotalOvertime()).isZero();
        assertThat(newMonth.getVacationDayCount()).isZero();
    }

    @Test
    void loadOrCreate_loadsExistingMonth()
    {
        final MonthData month = jsonMonth(YEAR_MONTH, Duration.ofMinutes(4));
        when(fileStorageMock.load(YEAR_MONTH)).thenReturn(Optional.of(month));

        final MonthIndex newMonth = storage.loadOrCreate(YEAR_MONTH);

        assertThat(newMonth.getYearMonth()).isEqualTo(YEAR_MONTH);
        assertThat(newMonth.getSortedDays()).hasSize(30);
        assertThat(newMonth.getTotalOvertime()).hasMinutes(4);
        assertThat(newMonth.getVacationDayCount()).isZero();
        assertThat(newMonth.getOvertimePreviousMonth()).hasMinutes(4);
    }

    @Test
    void storeMonth()
    {
        final MonthData month = jsonMonth(YEAR_MONTH, Duration.ofMinutes(4));
        when(monthIndexMock.getMonthRecord()).thenReturn(month);
        when(monthIndexMock.getYearMonth()).thenReturn(YEAR_MONTH);

        storage.storeMonth(monthIndexMock);

        verify(fileStorageMock).store(eq(YEAR_MONTH), same(month));
    }

    @Test
    void loadAll_empty()
    {
        when(fileStorageMock.loadAll()).thenReturn(emptyList());

        final MultiMonthIndex index = storage.loadAll();

        assertThat(index.getDays()).isEmpty();
        assertThat(index.getMonths()).isEmpty();
    }

    @Test
    void loadAll_nonEmpty()
    {
        when(fileStorageMock.loadAll()).thenReturn(List.of(jsonMonth(YEAR_MONTH, Duration.ZERO)));

        final MultiMonthIndex index = storage.loadAll();

        assertThat(index.getDays()).hasSize(30);
        assertThat(index.getMonths()).hasSize(1);
    }

    @Test
    void getAvailableDataYearMonth()
    {
        final List<YearMonth> availableYearMonths = List.of(YEAR_MONTH);
        when(fileStorageMock.getAvailableDataMonths()).thenReturn(availableYearMonths);

        assertThat(storage.getAvailableDataMonths()).isSameAs(availableYearMonths);
    }

    private MonthData jsonMonth(YearMonth yearMonth, Duration overtimePreviousMonth)
    {
        final MonthData month = new JsonModelFactory().createMonthData();
        month.setYear(yearMonth.getYear());
        month.setMonth(yearMonth.getMonth());
        month.setOvertimePreviousMonth(overtimePreviousMonth);
        month.setDays(new ArrayList<>());
        return month;
    }
}
