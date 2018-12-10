package org.itsallcode.whiterabbit.logic.model;

import java.util.List;
import java.util.stream.Stream;

public class MultiMonthIndex {

	private final List<MonthIndex> months;

	public MultiMonthIndex(List<MonthIndex> months) {
		this.months = months;
	}

	public List<MonthIndex> getMonths() {
		return months;
	}

	public Stream<DayRecord> getDays() {
		return months.stream().flatMap(MonthIndex::getSortedDays);
	}
}
