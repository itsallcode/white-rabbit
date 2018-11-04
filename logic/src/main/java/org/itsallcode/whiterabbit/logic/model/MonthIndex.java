package org.itsallcode.whiterabbit.logic.model;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;

public class MonthIndex {
	private final JsonMonth record;
	private final Map<LocalDate, DayRecord> days;

	private MonthIndex(JsonMonth record, Map<LocalDate, DayRecord> days) {
		this.record = record;
		this.days = days;
	}

	public static MonthIndex create(JsonMonth record) {
		final Map<LocalDate, DayRecord> days = record.getDays().stream() //
				.map(DayRecord::new) //
				.collect(toMap(DayRecord::getDate, Function.identity()));
		return new MonthIndex(record, days);
	}

	public LocalDate getFirstDayOfMonth() {
		return LocalDate.of(record.getYear(), record.getMonth(), 1);
	}

	public DayRecord getDay(LocalDate date) {
		return days.computeIfAbsent(date, this::createDay);
	}

	public void put(DayRecord day) {
		this.days.put(day.getDate(), day);
	}

	public JsonMonth getMonthRecord() {
		return JsonMonth.create(record, getSortedJsonDays());
	}

	public Stream<DayRecord> getSortedDays() {
		return days.values().stream() //
				.sorted(Comparator.comparing(DayRecord::getDate));
	}

	public List<JsonDay> getSortedJsonDays() {
		return getSortedDays().map(DayRecord::getJsonDay).collect(toList());
	}

	private DayRecord createDay(LocalDate date) {
		final JsonDay day = new JsonDay();
		day.setDate(date);
		return new DayRecord(day);
	}
}
