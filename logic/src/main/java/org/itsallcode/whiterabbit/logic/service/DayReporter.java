package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

public class DayReporter {
	private static final Logger LOG = LogManager.getLogger(DayReporter.class);
	private static final String NEWLINE = "\n";

	private final FormatterService formatterService;

	private final StringBuilder report = new StringBuilder();
	private Duration overtime = Duration.ZERO;

	public DayReporter(FormatterService formatterService) {
		this.formatterService = formatterService;
	}

	public void add(DayRecord day) {
		this.overtime = overtime.plus(day.getOvertime());
		final String line = formatterService.format(day);
		report.append(line).append(", Acc. overtime: ").append(overtime).append(NEWLINE);
	}

	public void finish() {
		report.append("Total overtime: ").append(overtime);
		LOG.info("Report\n{}", report.toString());
	}
}
