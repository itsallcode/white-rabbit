package org.itsallcode.whiterabbit.logic.storage;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;

class CachingStorage implements Storage
{
    private final Storage delegateStorage;
    private final MonthCache cache;

    CachingStorage(Storage delegateStorage)
    {
        this(delegateStorage, new MonthCache());
    }

    CachingStorage(Storage delegateStorage, MonthCache cache)
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
}
