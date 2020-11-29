package org.itsallcode.whiterabbit.logic.storage;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachingStorageImplTest
{
    private static final YearMonth YEAR_MONTH = YearMonth.of(2020, Month.NOVEMBER);

    @Mock
    MonthCache cacheMock;
    @Mock
    Storage delegateStorageMock;
    @Mock
    MonthIndex monthIndexMock1;
    @Mock
    MonthIndex monthIndexMock2;

    CachingStorageImpl storage;

    @BeforeEach
    void setUp()
    {
        storage = new CachingStorageImpl(delegateStorageMock, cacheMock);
    }

    @Test
    void loadMonth_updatesCache_whenMonthFound()
    {
        when(delegateStorageMock.loadMonth(YEAR_MONTH)).thenReturn(Optional.of(monthIndexMock1));
        storage.loadMonth(YEAR_MONTH);

        verifyListenerUpdated();
    }

    @Test
    void loadMonth_doesNotUpdateCache_whenMonthNotFound()
    {
        when(delegateStorageMock.loadMonth(YEAR_MONTH)).thenReturn(Optional.empty());
        storage.loadMonth(YEAR_MONTH);

        verifyNoInteractions(cacheMock);
    }

    @Test
    void loadOrCreate_updatesCache()
    {
        when(delegateStorageMock.loadOrCreate(YEAR_MONTH)).thenReturn(monthIndexMock1);
        storage.loadOrCreate(YEAR_MONTH);
        verifyListenerUpdated();
    }

    @Test
    void storeMonth_updatesCache()
    {
        storage.storeMonth(monthIndexMock1);
        verifyListenerUpdated();
    }

    @Test
    void loadAll_updatesCache_whenEntriesFound()
    {
        when(delegateStorageMock.loadAll()).thenReturn(new MultiMonthIndex(List.of(monthIndexMock1)));

        storage.loadAll();
        verifyListenerUpdated();
    }

    @Test
    void loadAll_doesNotUpdateCache_whenNoEntriesFound()
    {
        when(delegateStorageMock.loadAll()).thenReturn(new MultiMonthIndex(emptyList()));

        storage.loadAll();
        verifyNoInteractions(cacheMock);
    }

    @Test
    void getRequiredYearMonths_returnsEmptyList_whenNoDataAvailable()
    {
        when(delegateStorageMock.getAvailableDataYearMonth()).thenReturn(emptyList());

        assertThat(storage.getRequiredYearMonths(LocalDate.of(2020, Month.JANUARY, 1))).isEmpty();
    }

    @Test
    void getRequiredYearMonths_returnsRequiredMonths()
    {
        final YearMonth january = YearMonth.of(2020, Month.JANUARY);
        final YearMonth february = YearMonth.of(2020, Month.FEBRUARY);
        final YearMonth march = YearMonth.of(2020, Month.MARCH);
        final YearMonth april = YearMonth.of(2020, Month.APRIL);
        when(delegateStorageMock.getAvailableDataYearMonth()).thenReturn(List.of(january, february, march, april));

        assertThat(storage.getRequiredYearMonths(LocalDate.of(2020, Month.JANUARY, 1)))
                .containsExactly(january, february, march, april);
    }

    @Test
    void getRequiredYearMonths_includesMonthForLastDayOfTheMonth()
    {
        final YearMonth january = YearMonth.of(2020, Month.JANUARY);
        final YearMonth february = YearMonth.of(2020, Month.FEBRUARY);
        final YearMonth march = YearMonth.of(2020, Month.MARCH);
        final YearMonth april = YearMonth.of(2020, Month.APRIL);
        when(delegateStorageMock.getAvailableDataYearMonth()).thenReturn(List.of(january, february, march, april));

        assertThat(storage.getRequiredYearMonths(LocalDate.of(2020, Month.JANUARY, 31)))
                .containsExactly(january, february, march, april);
    }

    @Test
    void getRequiredYearMonths_omitsOlderMonths()
    {
        final YearMonth january = YearMonth.of(2020, Month.JANUARY);
        final YearMonth february = YearMonth.of(2020, Month.FEBRUARY);
        final YearMonth march = YearMonth.of(2020, Month.MARCH);
        final YearMonth april = YearMonth.of(2020, Month.APRIL);
        when(delegateStorageMock.getAvailableDataYearMonth()).thenReturn(List.of(january, february, march, april));

        assertThat(storage.getRequiredYearMonths(LocalDate.of(2020, Month.FEBRUARY, 1)))
                .containsExactly(february, march, april);
    }

    @Test
    void ensureLatestDaysCached_dataAvailable()
    {
        final YearMonth january = YearMonth.of(2020, Month.JANUARY);
        final YearMonth february = YearMonth.of(2020, Month.FEBRUARY);
        when(delegateStorageMock.getAvailableDataYearMonth()).thenReturn(List.of(january, february));

        when(cacheMock.contains(january)).thenReturn(false);
        when(cacheMock.contains(february)).thenReturn(false);

        when(delegateStorageMock.loadMonth(january)).thenReturn(Optional.of(monthIndexMock1));
        when(delegateStorageMock.loadMonth(february)).thenReturn(Optional.of(monthIndexMock2));

        storage.ensureLatestDaysCached(LocalDate.of(2020, Month.JANUARY, 1));

        final InOrder inOrder = inOrder(cacheMock, delegateStorageMock);
        inOrder.verify(delegateStorageMock).loadMonth(january);
        inOrder.verify(cacheMock).update(monthIndexMock1);
        inOrder.verify(delegateStorageMock).loadMonth(february);
        inOrder.verify(cacheMock).update(monthIndexMock2);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void ensureLatestDaysCached_dataAvailable_alreadyCached()
    {
        final YearMonth january = YearMonth.of(2020, Month.JANUARY);
        final YearMonth february = YearMonth.of(2020, Month.FEBRUARY);
        when(delegateStorageMock.getAvailableDataYearMonth()).thenReturn(List.of(january, february));

        when(cacheMock.contains(january)).thenReturn(true);
        when(cacheMock.contains(february)).thenReturn(true);

        storage.ensureLatestDaysCached(LocalDate.of(2020, Month.JANUARY, 1));

        verify(delegateStorageMock, never()).loadMonth(any());
        verify(cacheMock, never()).update(any());
    }

    @Test
    void ensureLatestDaysCached_cacheNotUpdatedWhenMonthNotFound()
    {
        final YearMonth january = YearMonth.of(2020, Month.JANUARY);
        final YearMonth february = YearMonth.of(2020, Month.FEBRUARY);
        when(delegateStorageMock.getAvailableDataYearMonth()).thenReturn(List.of(january, february));

        when(delegateStorageMock.loadMonth(january)).thenReturn(Optional.empty());
        when(delegateStorageMock.loadMonth(february)).thenReturn(Optional.empty());

        storage.ensureLatestDaysCached(LocalDate.of(2020, Month.JANUARY, 1));

        verify(cacheMock, never()).update(any());
    }

    @Test
    void getLatestDays_delegatesToCache()
    {

        final YearMonth january = YearMonth.of(2020, Month.JANUARY);
        final YearMonth february = YearMonth.of(2020, Month.FEBRUARY);
        when(delegateStorageMock.getAvailableDataYearMonth()).thenReturn(List.of(january, february));

        when(cacheMock.contains(january)).thenReturn(false);
        when(cacheMock.contains(february)).thenReturn(false);

        when(delegateStorageMock.loadMonth(january)).thenReturn(Optional.of(monthIndexMock1));
        when(delegateStorageMock.loadMonth(february)).thenReturn(Optional.of(monthIndexMock2));

        final LocalDate firstOfJanuary = LocalDate.of(2020, Month.JANUARY, 1);
        final List<DayRecord> days = new ArrayList<>();
        when(cacheMock.getLatestDays(firstOfJanuary)).thenReturn(days);

        assertThat(storage.getLatestDays(firstOfJanuary)).isSameAs(days);

        final InOrder inOrder = inOrder(cacheMock, delegateStorageMock);
        inOrder.verify(delegateStorageMock).loadMonth(january);
        inOrder.verify(cacheMock).update(monthIndexMock1);
        inOrder.verify(delegateStorageMock).loadMonth(february);
        inOrder.verify(cacheMock).update(monthIndexMock2);
        inOrder.verify(cacheMock).getLatestDays(firstOfJanuary);

        inOrder.verifyNoMoreInteractions();
    }

    private void verifyListenerUpdated()
    {
        verify(cacheMock).update(same(monthIndexMock1));
    }

    @Test
    void getAvailableDataYearMonth_delegates()
    {
        final List<YearMonth> list = new ArrayList<>();
        when(delegateStorageMock.getAvailableDataYearMonth()).thenReturn(list);
        assertThat(storage.getAvailableDataYearMonth()).isSameAs(list);
    }
}
