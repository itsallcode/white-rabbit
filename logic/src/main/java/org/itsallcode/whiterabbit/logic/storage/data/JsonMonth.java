package org.itsallcode.whiterabbit.logic.storage.data;

import java.time.Duration;
import java.time.Month;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.MonthData;

@JsonbPropertyOrder({ "year", "month", "overtimePreviousMonth", "days" })
public class JsonMonth implements MonthData
{
    @JsonbProperty("year")
    private int year;
    @JsonbProperty("month")
    private Month month;
    @JsonbProperty("overtimePreviousMonth")
    private Duration overtimePreviousMonth;
    @JsonbProperty("days")
    private List<DayData> days;

    @Override
    public int getYear()
    {
        return year;
    }

    @Override
    public void setYear(int year)
    {
        this.year = year;
    }

    @Override
    public Month getMonth()
    {
        return month;
    }

    @Override
    public void setMonth(Month month)
    {
        this.month = month;
    }

    @Override
    public Duration getOvertimePreviousMonth()
    {
        return overtimePreviousMonth;
    }

    @Override
    public void setOvertimePreviousMonth(Duration overtimePreviousMonth)
    {
        this.overtimePreviousMonth = overtimePreviousMonth;
    }

    @Override
    public List<DayData> getDays()
    {
        return days;
    }

    @Override
    public void setDays(List<DayData> days)
    {
        this.days = days;
    }
}
