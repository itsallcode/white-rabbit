package org.itsallcode.whiterabbit.api.features;

import java.time.LocalDate;
import java.util.List;

import org.itsallcode.whiterabbit.api.model.DayType;

/**
 * {@link PluginFeature} that generates a {@link List} of
 * {@link HolidayInstance} for each day. WhiteRabbit will will marked these days
 * as {@link DayType#HOLIDAY} when switching to a new month.
 */
public interface Holidays extends PluginFeature
{
    /**
     * Get all holidays for a given date.
     * 
     * @param date
     *            the date for which to get holidays.
     * @return a {@link List} of holidays on the given date.
     */
    List<HolidayInstance> getHolidays(LocalDate date);

    /**
     * Represents a holiday instance.
     */
    interface HolidayInstance
    {
        /**
         * @return the holiday category, e.g. "public".
         */
        String getCategory();

        /**
         * @return the local name of the holiday.
         */
        String getName();

        /**
         * @return the date of the holiday.
         */
        LocalDate getDate();
    }
}
