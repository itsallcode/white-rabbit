package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;
import java.time.Month;
import java.util.List;

/**
 * Model class for data of a single month containing year, month, day data etc.
 */
public interface MonthData
{
    /**
     * Get the year of this month.
     * 
     * @return the year of this month.
     */
    int getYear();

    /**
     * Set the year of this month.
     * 
     * @param year
     *            the new year.
     */
    void setYear(int year);

    /**
     * Get the {@link Month}.
     * 
     * @return the {@link Month}.
     */
    Month getMonth();

    /**
     * Set a new {@link Month}.
     * 
     * @param month
     *            the new {@link Month}.
     */
    void setMonth(Month month);

    /**
     * Get the duration of the previous month's overtime.
     * 
     * @return the duration of the previous month's overtime.
     */
    Duration getOvertimePreviousMonth();

    /**
     * Set the duration of the previous month's overtime.
     * 
     * @param overtimePreviousMonth
     *            the new duration of the previous month's overtime.
     */
    void setOvertimePreviousMonth(Duration overtimePreviousMonth);

    /**
     * Get the {@link DayData}s in this month.
     * 
     * @return the {@link DayData}s in this month.
     */
    List<DayData> getDays();

    /**
     * Set the {@link DayData}s in this month.
     * 
     * @param days
     *            the new {@link DayData}s.
     */
    void setDays(List<DayData> days);
}