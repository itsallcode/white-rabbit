package org.itsallcode.whiterabbit.logic.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.stream.Stream;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MonthCacheTest
{
    private static final LocalDate VERY_OLD = LocalDate.of(1900, Month.JANUARY, 1);
    private static final LocalDate RECENT = LocalDate.of(2020, Month.SEPTEMBER, 15);

    MonthCache cache;

    @BeforeEach
    void setUp()
    {
        cache = new MonthCache();
    }

    @Test
    void getLatestDays_emptyCache_returnsEmptyList()
    {
        assertThat(cache.getLatestDays(VERY_OLD)).isEmpty();
    }

    @Test
    void getLatestDays_singleResult()
    {
        final DayRecord day1 = day(LocalDate.of(2020, Month.OCTOBER, 25));
        cache.update(month(YearMonth.of(2020, Month.OCTOBER), day1));
        assertThat(cache.getLatestDays(VERY_OLD)).containsExactly(day1);
    }

    @Test
    void updateOverwritesExistingEntry()
    {
        final DayRecord day1 = day(LocalDate.of(2020, Month.OCTOBER, 25));
        final DayRecord day2 = day(LocalDate.of(2020, Month.OCTOBER, 26));

        cache.update(month(YearMonth.of(2020, Month.OCTOBER), day1));
        assertThat(cache.getLatestDays(VERY_OLD)).containsExactly(day1);

        cache.update(month(YearMonth.of(2020, Month.OCTOBER), day2));
        assertThat(cache.getLatestDays(VERY_OLD)).containsExactly(day2);
    }

    @Test
    void getLatestDays_filtersOldMonths()
    {
        final DayRecord day1 = day(LocalDate.of(2020, Month.OCTOBER, 25));
        cache.update(month(YearMonth.from(RECENT).minusMonths(1), day1));

        assertThat(cache.getLatestDays(RECENT)).isEmpty();
    }

    @Test
    void getLatestDays_includesExactMonth()
    {
        final DayRecord day1 = day(LocalDate.of(2020, Month.OCTOBER, 25));
        cache.update(month(YearMonth.from(RECENT), day1));

        assertThat(cache.getLatestDays(RECENT)).containsExactly(day1);
    }

    @Test
    void getLatestDays_filtersOldDays()
    {
        final DayRecord day1 = day(RECENT.minusDays(1));
        cache.update(month(YearMonth.from(RECENT), day1));

        assertThat(cache.getLatestDays(RECENT)).isEmpty();
    }

    @Test
    void getLatestDays_includesExactDay()
    {
        final DayRecord day1 = day(RECENT);
        cache.update(month(YearMonth.from(RECENT), day1));

        assertThat(cache.getLatestDays(RECENT)).containsExactly(day1);
    }

    private DayRecord day(LocalDate date)
    {
        final DayRecord day = mock(DayRecord.class);
        when(day.getDate()).thenReturn(date);
        return day;
    }

    private MonthIndex month(YearMonth yearMonth, DayRecord... days)
    {
        final MonthIndex monthMock = mock(MonthIndex.class);
        when(monthMock.getYearMonth()).thenReturn(yearMonth);
        when(monthMock.getSortedDays()).thenReturn(Stream.of(days));
        return monthMock;
    }

}
