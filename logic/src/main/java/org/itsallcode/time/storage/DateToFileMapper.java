package org.itsallcode.time.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.time.model.MonthIndex;

public class DateToFileMapper {
	private static final Logger LOG = LogManager.getLogger(DateToFileMapper.class);

	private final Path dataDir;
	private final DateTimeFormatter formatter;

	public DateToFileMapper(Path dataDir) {
		formatter = DateTimeFormatter.ofPattern("yyyy-MM", Locale.ENGLISH);
		this.dataDir = dataDir;
	}

	public Path getPathForDate(MonthIndex month) {
		return getPathForDate(month.getFirstDayOfMonth());
	}

	public Path getPathForDate(LocalDate date) {
		return dataDir.resolve(getFileName(date));
	}

	private String getFileName(LocalDate date) {
		return date.format(formatter) + ".json";
	}

	public Stream<Path> getAllFiles() {
		LOG.debug("Reading all files in {}", dataDir);
		try {
			return Files.walk(dataDir) //
					.filter(file -> !file.toFile().isDirectory()) //
					.filter(file -> file.getFileName().toString().endsWith(".json"));
		} catch (final IOException e) {
			throw new IllegalStateException("Error listing directory " + dataDir, e);
		}
	}
}
