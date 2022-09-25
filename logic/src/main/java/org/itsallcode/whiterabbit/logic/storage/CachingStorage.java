package org.itsallcode.whiterabbit.logic.storage;

import java.time.LocalDate;
import java.util.List;

import org.itsallcode.whiterabbit.api.features.MonthDataStorage;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.holidays.HolidayService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

/**
 * This extends the {@link Storage} interface with methods required for caching
 * data.
 */
public interface CachingStorage extends Storage
{
    /**
     * Creates a new {@link CachingStorage} instance.
     * 
     * @return a new {@link CachingStorage} instance
     */
    static CachingStorage create(final MonthDataStorage dataStorage, final ContractTermsService contractTerms,
            final ProjectService projectService, final HolidayService holidayService)
    {
        final MonthIndexStorage monthIndexStorage = new MonthIndexStorage(contractTerms, projectService, dataStorage,
                holidayService);
        return new CachingStorageImpl(monthIndexStorage);
    }

    /**
     * Get all available day records younger than the given date.
     * 
     * @param maxAge
     *            the maximum age of the records to return
     * @return all available day records younger than the given date.
     */
    List<DayRecord> getLatestDays(LocalDate maxAge);

    /**
     * Add a {@link CacheInvalidationListener} that will be called when the
     * cache is updated.
     * 
     * @param listener
     *            the listener to add
     */
    void addCacheInvalidationListener(CacheInvalidationListener listener);

    /**
     * A listener that is called when the cache is updated.
     */
    public interface CacheInvalidationListener
    {
        /**
         * Called when the cache is updated.
         * 
         * @param updatedMonth
         *            the new data
         */
        void cacheUpdated(MonthIndex updatedMonth);
    }
}
