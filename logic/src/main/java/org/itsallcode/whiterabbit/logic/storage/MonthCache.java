package org.itsallcode.whiterabbit.logic.storage;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.storage.CachingStorage.CacheInvalidationListener;

class MonthCache
{
    private final List<CacheInvalidationListener> listeners = new ArrayList<>();
    private final TreeMap<YearMonth, MonthIndex> cache = new TreeMap<>(
            Comparator.<YearMonth> naturalOrder().reversed());

    void update(final MonthIndex month)
    {
        cache.put(month.getYearMonth(), month);
        notifyListeners(month);
    }

    boolean contains(final YearMonth month)
    {
        return cache.containsKey(month);
    }

    List<DayRecord> getLatestDayRecords(final LocalDate maxAge)
    {
        final YearMonth oldestYearMonth = YearMonth.from(maxAge);
        return cache.values().stream()
                .filter(month -> !month.getYearMonth().isBefore(oldestYearMonth))
                .flatMap(MonthIndex::getSortedDays)
                .filter(day -> !day.getDate().isBefore(maxAge))
                .toList();
    }

    private void notifyListeners(final MonthIndex updatedMonth)
    {
        listeners.forEach(listener -> listener.cacheUpdated(updatedMonth));
    }

    public void addCacheInvalidationListener(final CacheInvalidationListener listener)
    {
        listeners.add(listener);
    }
}
