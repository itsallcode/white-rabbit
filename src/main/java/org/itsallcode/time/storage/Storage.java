package org.itsallcode.time.storage;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.time.model.MonthIndex;
import org.itsallcode.time.model.MultiMonthIndex;
import org.itsallcode.time.model.json.JsonMonth;

public class Storage {
	private static final Logger LOG = LogManager.getLogger(Storage.class);

	private final Jsonb jsonb;
	private final DateToFileMapper dateToFileMapper;

	private Storage(DateToFileMapper dateToFileMapper, Jsonb jsonb) {
		this.dateToFileMapper = dateToFileMapper;
		this.jsonb = jsonb;
	}

	public Storage(DateToFileMapper dateToFileMapper) {
		this(dateToFileMapper, JsonbBuilder.create(new JsonbConfig().withFormatting(true)));
	}

	public MonthIndex loadMonth(LocalDate date) {
		return MonthIndex.create(loadMonthRecord(date));
	}

	public void storeMonth(MonthIndex month) {
		writeToFile(month);
	}

	public MultiMonthIndex loadAll() {
		final List<MonthIndex> months = dateToFileMapper.getAllFiles() //
				.map(this::loadFromFile) //
				.map(MonthIndex::create) //
				.sorted(Comparator.comparing(MonthIndex::getFirstDayOfMonth)) //
				.collect(toList());
		return new MultiMonthIndex(months);
	}

	private void writeToFile(MonthIndex month) {
		final Path file = dateToFileMapper.getPathForDate(month);
		createDirectory(file.getParent());
		try (OutputStream stream = Files.newOutputStream(file, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			jsonb.toJson(month.getMonthRecord(), stream);
		} catch (final IOException e) {
			throw new IllegalStateException("Error writing file " + file, e);
		}
	}

	private void createDirectory(Path dir) {
		if (dir.toFile().isDirectory()) {
			return;
		}
		try {
			Files.createDirectories(dir);
		} catch (final IOException e) {
			throw new IllegalStateException("Error creating dir " + dir, e);
		}
	}

	private JsonMonth loadMonthRecord(LocalDate date) {
		final Path file = dateToFileMapper.getPathForDate(date);
		if (file.toFile().exists()) {
			return loadFromFile(file);
		}
		return createNewMonth(date);
	}

	private JsonMonth createNewMonth(LocalDate date) {
		final JsonMonth month = new JsonMonth();
		month.setYear(date.getYear());
		month.setMonth(date.getMonth());
		month.setDays(new ArrayList<>());
		return month;
	}

	private JsonMonth loadFromFile(Path file) {
		LOG.debug("Reading file {}", file);
		try (InputStream stream = Files.newInputStream(file)) {
			return jsonb.fromJson(stream, JsonMonth.class);
		} catch (final IOException e) {
			throw new IllegalStateException("Error reading file " + file, e);
		}
	}
}
