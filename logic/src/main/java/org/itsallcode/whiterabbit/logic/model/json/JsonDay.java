package org.itsallcode.whiterabbit.logic.model.json;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "date", "type", "begin", "end", "interruption", "comment" })
public class JsonDay {

    @JsonbProperty("date")
    private LocalDate date;
    @JsonbProperty("type")
    private DayType type;
    @JsonbProperty("begin")
    private LocalTime begin;
    @JsonbProperty("end")
    private LocalTime end;
    @JsonbProperty("interruption")
    private Duration interruption;
    @JsonbProperty("comment")
    private String comment;

    public LocalDate getDate() {
	return date;
    }

    public DayType getType() {
	return type;
    }

    public LocalTime getBegin() {
	return begin;
    }

    public LocalTime getEnd() {
	return end;
    }

    public Duration getInterruption() {
	return interruption;
    }

    public String getComment() {
	return comment;
    }

    public void setDate(LocalDate date) {
	this.date = date;
    }

    public void setType(DayType type) {
	this.type = type;
    }

    public void setBegin(LocalTime begin) {
	this.begin = begin;
    }

    public void setEnd(LocalTime end) {
	this.end = end;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    public void setInterruption(Duration interruption) {
	this.interruption = interruption;
    }
}
