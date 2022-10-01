package org.itsallcode.whiterabbit.logic.storage;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;

/**
 * Storage allows implementing different backends for storing and retrieving
 * month data.
 */
public interface Storage
{
    /**
     * Load the data for the given month.
     * 
     * @param yearMonth
     *            the month for which to load the data
     * @return month data or an empty {@link Optional} if no data is available.
     */
    Optional<MonthIndex> loadMonth(YearMonth yearMonth);

    /**
     * Load the data for the given month or create a new entry if it does not
     * exist yet.
     * 
     * @param yearMonth
     *            the month for which to load the data
     * @return month data.
     */
    MonthIndex loadOrCreate(YearMonth yearMonth);

    /**
     * Store the given data.
     * 
     * @param month
     *            the data to store
     */
    void storeMonth(MonthIndex month);

    /**
     * Load available data for all months.
     * 
     * @return all available data
     */
    MultiMonthIndex loadAll();

    /**
     * Get the months for which data is available.
     * 
     * @return months for which data is available
     */
    List<YearMonth> getAvailableDataMonths();
}