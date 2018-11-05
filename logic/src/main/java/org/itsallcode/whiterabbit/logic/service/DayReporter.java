package org.itsallcode.whiterabbit.logic.service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.format.TextStyle;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

public class DayReporter {
	private static final Logger LOG = LogManager.getLogger(DayReporter.class);
	private static final String NEWLINE = "\n";
	private static final Locale LOCALE = Locale.US;

	private final StringBuilder report = new StringBuilder();
	private Duration overtime = Duration.ZERO;

	public void add(DayRecord day) {
		this.overtime = overtime.plus(day.getOvertime());
		final String dayOfWeek = day.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, LOCALE);
		final String workingDay = day.isWorkingDay() ? "work   " : "no work";
		final String date = MessageFormat.format("{0} {1} {2}\t{3}", day.getDate(), dayOfWeek, day.getType(), workingDay);

		final String time = day.getBegin() != null ? MessageFormat.format("{0} - {1}", day.getBegin(), day.getEnd()) : "             ";
		final String line = MessageFormat.format("{0} {1} break: {2}, interr.: {3}, working time: {4}, overtime: {5}, overtime acc: {6}", date, time,
				day.getMandatoryBreak(), day.getInterruption(), day.getWorkingTime(), day.getOvertime(), overtime);
		report.append(line).append(NEWLINE);
	}

	public void finish() {
		LOG.info("Report\n{}", report.toString());
	}
}
