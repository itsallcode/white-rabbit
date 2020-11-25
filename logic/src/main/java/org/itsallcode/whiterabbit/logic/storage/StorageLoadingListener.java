package org.itsallcode.whiterabbit.logic.storage;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;

@FunctionalInterface
public interface StorageLoadingListener
{
    void monthLoaded(MonthIndex month);
}
