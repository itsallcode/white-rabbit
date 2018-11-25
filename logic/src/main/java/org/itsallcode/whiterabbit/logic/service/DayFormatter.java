package org.itsallcode.whiterabbit.logic.service;

import java.text.MessageFormat;
import java.time.format.TextStyle;
import java.util.Locale;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

public class DayFormatter {

	private final Locale locale;

	public DayFormatter(Locale locale) {
		this.locale = locale;
	}

	public String format(DayRecord day) {
		final String dayOfWeek = day.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, locale);
		final String date = MessageFormat.format("{0} {1} {2}", day.getDate(), dayOfWeek, day.getType());
		final String time = day.getBegin() != null ? MessageFormat.format("{0} - {1}", day.getBegin(), day.getEnd()) : "             ";
		return MessageFormat.format("{0} {1} break: {2}, interr.: {3}, working time: {4}, overtime: {5}", date, time, day.getMandatoryBreak(),
				day.getInterruption(), day.getWorkingTime(), day.getOvertime());
	}
}
