package org.itsallcode.whiterabbit.logic.model.json;

import java.time.Duration;
import java.time.Month;
import java.util.List;

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