package org.itsallcode.whiterabbit.logic.model.json;

import java.time.Duration;
import java.time.Month;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "year", "month", "overtimePreviousMonth", "days" })
public class JsonMonth
{
    @JsonbProperty("year")
    private int year;
    @JsonbProperty("month")
    private Month month;
    @JsonbProperty("overtimePreviousMonth")
    private Duration overtimePreviousMonth;
    @JsonbProperty("days")
    private List<JsonDay> days;

    public static JsonMonth create(JsonMonth record, List<JsonDay> sortedDays)
    {
        final JsonMonth month = new JsonMonth();
        month.setOvertimePreviousMonth(record.getOvertimePreviousMonth());
        month.setYear(record.getYear());
        month.setMonth(record.getMonth());
        month.setDays(sortedDays);
        return month;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public Month getMonth()
    {
        return month;
    }

    public void setMonth(Month month)
    {
        this.month = month;
    }

    public Duration getOvertimePreviousMonth()
    {
        return overtimePreviousMonth;
    }

    public void setOvertimePreviousMonth(Duration overtimePreviousMonth)
    {
        this.overtimePreviousMonth = overtimePreviousMonth;
    }

    public List<JsonDay> getDays()
    {
        return days;
    }

    public void setDays(List<JsonDay> days)
    {
        this.days = days;
    }
}
