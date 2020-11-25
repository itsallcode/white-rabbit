package org.itsallcode.whiterabbit.logic.autocomplete;

import org.itsallcode.whiterabbit.logic.storage.Storage;

public class AutocompleteService
{
    private final MonthCache monthCache;
    private final Storage storage;

    private boolean cacheInitialized = false;

    public AutocompleteService(Storage storage, MonthCache monthCache)
    {
        this.storage = storage;
        this.monthCache = monthCache;
    }

    private void ensureCacheInitialized()
    {
        if (cacheInitialized)
        {
            return;
        }
        storage.loadAll();
        cacheInitialized = true;
    }
}
