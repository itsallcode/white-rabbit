package org.itsallcode.whiterabbit.logic.storage;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;

class CachingStorageImpl implements CachingStorage
{
    private static final Logger LOG = LogManager.getLogger(CachingStorageImpl.class);

    private final Storage delegateStorage;
    private final MonthCache cache;

    CachingStorageImpl(Storage delegateStorage)
    {
        this(delegateStorage, new MonthCache());
    }

    CachingStorageImpl(Storage delegateStorage, MonthCache cache)
    {
        this.delegateStorage = delegateStorage;
        this.cache = cache;
    }

    @Override
    public Optional<MonthIndex> loadMonth(YearMonth date)
    {
        return delegateStorage.loadMonth(date).map(this::updateCache);
    }

    @Override
    public MonthIndex loadOrCreate(final YearMonth yearMonth)
    {
        return updateCache(delegateStorage.loadOrCreate(yearMonth));
    }

    @Override
    public void storeMonth(MonthIndex month)
    {
        delegateStorage.storeMonth(updateCache(month));
    }

    @Override
    public MultiMonthIndex loadAll()
    {
        return updateCache(delegateStorage.loadAll());
    }

    private MultiMonthIndex updateCache(MultiMonthIndex index)
    {
        index.getMonths().forEach(this::updateCache);
        return index;
    }

    private MonthIndex updateCache(MonthIndex month)
    {
        cache.update(month);
        return month;
    }

    @Override
    public List<YearMonth> getAvailableDataYearMonth()
    {
        return delegateStorage.getAvailableDataYearMonth();
    }

    @Override
    public List<DayRecord> getLatestDays(LocalDate maxAge)
    {
        ensureLatestDaysCached(maxAge);
        return cache.getLatestDays(maxAge);
    }

    void ensureLatestDaysCached(LocalDate maxAge)
    {
        for (final YearMonth requiredMonth : getRequiredYearMonths(maxAge))
        {
            if (!cache.contains(requiredMonth))
            {
                LOG.debug("Loading month {} into cache", requiredMonth);
                delegateStorage.loadMonth(requiredMonth).ifPresent(cache::update);
            }
        }
    }

    List<YearMonth> getRequiredYearMonths(LocalDate maxAge)
    {
        final YearMonth oldestYearMonth = YearMonth.from(maxAge);
        return delegateStorage.getAvailableDataYearMonth().stream()
                .filter(month -> !month.isBefore(oldestYearMonth))
                .collect(toList());
    }
}
