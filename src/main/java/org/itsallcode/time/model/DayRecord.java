package org.itsallcode.time.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.itsallcode.time.model.json.DayType;
import org.itsallcode.time.model.json.JsonDay;

public class DayRecord {
	private static final Duration BASIC_BREAK = Duration.ofMinutes(30);

	private final JsonDay day;

	public DayRecord(JsonDay day) {
		this.day = day;
	}

	public Duration getMandatoryBreak() {
		if (!isWorkingDay()) {
			return Duration.ZERO;
		}
		final Duration totalWorkingTime = getWorkingTime();
		if (totalWorkingTime.compareTo(Duration.ofHours(6)) > 0) {
			return BASIC_BREAK;
		}
		return Duration.ZERO;
	}

	public Duration getInterruption() {
		return day.getInterruption() == null ? Duration.ZERO : day.getInterruption();
	}

	public Duration getMandatoryWorkingTime() {
		if (isWorkingDay()) {
			return Duration.ofHours(8);
		} else {
			return Duration.ZERO;
		}
	}

	public Duration getWorkingTime() {
		if (getBegin() == null && getEnd() == null) {
			return Duration.ZERO;
		}
		if (getBegin() == null || getEnd() == null) {
			throw new IllegalStateException("Begin or end is null for " + day);
		}
		return Duration.between(getBegin(), getEnd());
	}

	public Duration getOvertime() {
		return getWorkingTime() //
				.minus(getMandatoryWorkingTime()) //
				.minus(getMandatoryBreak()) //
				.minus(getInterruption());
	}

	public LocalDate getDate() {
		return day.getDate();
	}

	public JsonDay getJsonDay() {
		return day;
	}

	public LocalTime getBegin() {
		return day.getBegin();
	}

	public LocalTime getEnd() {
		return day.getEnd();
	}

	public void setBegin(LocalTime begin) {
		day.setBegin(begin);
	}

	public void setEnd(LocalTime end) {
		day.setEnd(end);
	}

	public DayType getType() {
		if (day.getType() != null) {
			return day.getType();
		}
		if (isWeekend()) {
			return DayType.WEEKEND;
		}
		return DayType.WORK;
	}

	public boolean isWorkingDay() {
		return getType().isWorkDay();
	}

	private boolean isWeekend() {
		switch (day.getDate().getDayOfWeek()) {
		case SATURDAY:
		case SUNDAY:
			return true;
		default:
			return false;
		}
	}
}
