package org.itsallcode.whiterabbit.logic.storage;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import static java.util.stream.Collectors.toList;

class MonthCache
{
    private final TreeMap<YearMonth, MonthIndex> cache = new TreeMap<>(
            Comparator.<YearMonth> naturalOrder().reversed());

    void update(MonthIndex month)
    {
        cache.put(month.getYearMonth(), month);
    }

    boolean contains(YearMonth month)
    {
        return cache.containsKey(month);
    }

    List<DayRecord> getLatestDays(LocalDate maxAge)
    {
        final YearMonth oldestYearMonth = YearMonth.from(maxAge);
        return cache.values().stream()
                .filter(month -> !month.getYearMonth().isBefore(oldestYearMonth))
                .flatMap(MonthIndex::getSortedDays)
                .filter(day -> !day.getDate().isBefore(maxAge))
                .collect(toList());
    }
}
