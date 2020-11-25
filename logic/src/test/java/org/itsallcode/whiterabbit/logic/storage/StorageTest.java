package org.itsallcode.whiterabbit.logic.storage;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageTest
{
    private static final YearMonth YEAR_MONTH = YearMonth.of(2020, Month.NOVEMBER);

    @Mock
    StorageLoadingListener loadingListenerMock;
    @Mock
    MonthIndexStorage monthIndexStorageMock;
    @Mock
    MonthIndex monthIndexMock;

    Storage storage;

    @BeforeEach
    void setUp()
    {
        storage = new Storage(monthIndexStorageMock, loadingListenerMock);
    }

    @Test
    void loadMonth_updatesCache_whenMonthFound()
    {
        when(monthIndexStorageMock.loadMonth(YEAR_MONTH)).thenReturn(Optional.of(monthIndexMock));
        storage.loadMonth(YEAR_MONTH);

        verifyListenerUpdated();
    }

    @Test
    void loadMonth_doesNotUpdateCache_whenMonthNotFound()
    {
        when(monthIndexStorageMock.loadMonth(YEAR_MONTH)).thenReturn(Optional.empty());
        storage.loadMonth(YEAR_MONTH);

        verifyNoInteractions(loadingListenerMock);
    }

    @Test
    void loadOrCreate_updatesCache()
    {
        when(monthIndexStorageMock.loadOrCreate(YEAR_MONTH)).thenReturn(monthIndexMock);
        storage.loadOrCreate(YEAR_MONTH);
        verifyListenerUpdated();
    }

    @Test
    void storeMonth_updatesCache()
    {
        storage.storeMonth(monthIndexMock);
        verifyListenerUpdated();
    }

    @Test
    void loadAll_updatesCache_whenEntriesFound()
    {
        when(monthIndexStorageMock.loadAll()).thenReturn(new MultiMonthIndex(List.of(monthIndexMock)));

        storage.loadAll();
        verifyListenerUpdated();
    }

    @Test
    void loadAll_doesNotUpdateCache_whenNoEntriesFound()
    {
        when(monthIndexStorageMock.loadAll()).thenReturn(new MultiMonthIndex(emptyList()));

        storage.loadAll();
        verifyNoInteractions(loadingListenerMock);
    }

    private void verifyListenerUpdated()
    {
        verify(loadingListenerMock).monthLoaded(same(monthIndexMock));
    }

    @Test
    void getAvailableDataYearMonth_delegates()
    {
        final List<YearMonth> list = new ArrayList<>();
        when(monthIndexStorageMock.getAvailableDataYearMonth()).thenReturn(list);
        assertThat(storage.getAvailableDataYearMonth()).isSameAs(list);
    }
}
