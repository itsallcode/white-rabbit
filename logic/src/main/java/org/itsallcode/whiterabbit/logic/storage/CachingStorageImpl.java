package org.itsallcode.whiterabbit.logic.storage;

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

    CachingStorageImpl(final Storage delegateStorage)
    {
        this(delegateStorage, new MonthCache());
    }

    CachingStorageImpl(final Storage delegateStorage, final MonthCache cache)
    {
        this.delegateStorage = delegateStorage;
        this.cache = cache;
    }

    @Override
    public Optional<MonthIndex> loadMonth(final YearMonth date)
    {
        return delegateStorage.loadMonth(date).map(this::updateCache);
    }

    @Override
    public MonthIndex loadOrCreate(final YearMonth yearMonth)
    {
        return updateCache(delegateStorage.loadOrCreate(yearMonth));
    }

    @Override
    public void storeMonth(final MonthIndex month)
    {
        delegateStorage.storeMonth(updateCache(month));
    }

    @Override
    public MultiMonthIndex loadAll()
    {
        return updateCache(delegateStorage.loadAll());
    }

    private MultiMonthIndex updateCache(final MultiMonthIndex index)
    {
        index.getMonths().forEach(this::updateCache);
        return index;
    }

    private MonthIndex updateCache(final MonthIndex month)
    {
        cache.update(month);
        return month;
    }

    @Override
    public List<YearMonth> getAvailableDataMonths()
    {
        return delegateStorage.getAvailableDataMonths();
    }

    @Override
    public List<DayRecord> getLatestDays(final LocalDate maxAge)
    {
        ensureLatestDaysCached(maxAge);
        return cache.getLatestDayRecords(maxAge);
    }

    void ensureLatestDaysCached(final LocalDate maxAge)
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

    List<YearMonth> getRequiredYearMonths(final LocalDate maxAge)
    {
        final YearMonth oldestYearMonth = YearMonth.from(maxAge);
        return delegateStorage.getAvailableDataMonths().stream()
                .filter(month -> !month.isBefore(oldestYearMonth))
                .toList();
    }

    @Override
    public void addCacheInvalidationListener(final CacheInvalidationListener listener)
    {
        this.cache.addCacheInvalidationListener(listener);

    }
}
