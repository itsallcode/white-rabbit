package org.itsallcode.whiterabbit.logic.model.json;

import java.time.Month;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "year", "month", "days" })
public class JsonMonth {
    @JsonbProperty("year")
    private int year;
    @JsonbProperty("month")
    private Month month;
    @JsonbProperty("days")
    private List<JsonDay> days;

    public static JsonMonth create(JsonMonth record, List<JsonDay> sortedDays) {
	final JsonMonth month = new JsonMonth();
	month.setYear(record.getYear());
	month.setMonth(record.getMonth());
	month.setDays(sortedDays);
	return month;
    }

    public int getYear() {
	return year;
    }

    public void setYear(int year) {
	this.year = year;
    }

    public Month getMonth() {
	return month;
    }

    public void setMonth(Month month) {
	this.month = month;
    }

    public List<JsonDay> getDays() {
	return days;
    }

    public void setDays(List<JsonDay> days) {
	this.days = days;
    }
}
