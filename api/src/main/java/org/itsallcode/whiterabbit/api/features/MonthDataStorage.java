package org.itsallcode.whiterabbit.api.features;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.api.model.ActivityData;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.MonthData;

/**
 * {@link PluginFeature} that provides a storage backend for month data. This
 * class may keep the data on the local disk or on a backend server.
 * {@link YearMonth} is used as key for loading and storing entries.
 */
public interface MonthDataStorage extends PluginFeature
{
    /**
     * @param month
     *            the month for which to load the data.
     * @return the data for the given month.
     */
    Optional<MonthData> load(YearMonth month);

    /**
     * Store the data for a given month.
     * 
     * @param month
     *            the month.
     * @param data
     *            the data to store.
     */
    void store(YearMonth month, MonthData data);

    /**
     * @return all months for that data is available in the store.
     */
    List<YearMonth> getAvailableDataMonths();

    /**
     * @return all available data.
     */
    List<MonthData> loadAll();

    /**
     * @return the model factory that can be used to create instances of the
     *         model.
     */
    ModelFactory getModelFactory();

    /**
     * A {@link ModelFactory} allows creating new instances of the data model.
     */
    public interface ModelFactory
    {
        /**
         * @return a new {@link MonthData} instance.
         */
        MonthData createMonthData();

        /**
         * @return a new {@link DayData} instance.
         */
        DayData createDayData();

        /**
         * @return a new {@link ActivityData} instance.
         */
        ActivityData createActivityData();
    }
}