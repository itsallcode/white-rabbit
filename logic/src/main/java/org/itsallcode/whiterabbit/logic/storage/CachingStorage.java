package org.itsallcode.whiterabbit.logic.storage;

import java.time.LocalDate;
import java.util.List;

import org.itsallcode.whiterabbit.api.features.MonthDataStorage;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.holidays.HolidayService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

public interface CachingStorage extends Storage
{
    static CachingStorage create(MonthDataStorage dataStorage, ContractTermsService contractTerms,
            ProjectService projectService, HolidayService holidayService)
    {
        final MonthIndexStorage monthIndexStorage = new MonthIndexStorage(contractTerms, projectService, dataStorage,
                holidayService);
        return new CachingStorageImpl(monthIndexStorage);
    }

    List<DayRecord> getLatestDays(LocalDate maxAge);

    void addCacheInvalidationListener(CacheInvalidationListener listener);

    public interface CacheInvalidationListener
    {
        void cacheUpdated(MonthIndex updatedMonth);
    }
}
