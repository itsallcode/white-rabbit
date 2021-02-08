package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;
import java.time.Month;
import java.util.List;

/**
 * Model class for data of a single month containing year, month, day data etc.
 */
public interface MonthData
{
    int getYear();

    void setYear(int year);

    Month getMonth();

    void setMonth(Month month);

    Duration getOvertimePreviousMonth();

    void setOvertimePreviousMonth(Duration overtimePreviousMonth);

    List<DayData> getDays();

    void setDays(List<DayData> days);
}