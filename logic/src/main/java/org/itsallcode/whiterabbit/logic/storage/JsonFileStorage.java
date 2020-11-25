package org.itsallcode.whiterabbit.logic.storage;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import javax.json.bind.Jsonb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;

public class JsonFileStorage
{
    private static final Logger LOG = LogManager.getLogger(JsonFileStorage.class);

    private final Jsonb jsonb;
    private final DateToFileMapper dateToFileMapper;

    JsonFileStorage(Jsonb jsonb, DateToFileMapper dateToFileMapper)
    {
        this.jsonb = jsonb;
        this.dateToFileMapper = dateToFileMapper;
    }

    private JsonMonth loadFromFile(Path file)
    {
        LOG.trace("Reading file {}", file);
        try (InputStream stream = Files.newInputStream(file))
        {
            return jsonb.fromJson(stream, JsonMonth.class);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error reading file " + file, e);
        }
    }

    Optional<JsonMonth> loadMonthRecord(YearMonth date)
    {
        final Path file = dateToFileMapper.getPathForDate(date);
        if (file.toFile().exists())
        {
            LOG.trace("Found file {} for month {}", file, date);
            return Optional.of(loadFromFile(file));
        }
        final Path legacyFile = dateToFileMapper.getLegacyPathForDate(date);
        if (legacyFile.toFile().exists())
        {
            LOG.trace("Found legacy file {} for month {}", file, date);
            return Optional.of(loadFromFile(legacyFile));
        }
        LOG.debug("File {} not found for month {}", file, date);
        return Optional.empty();
    }

    void writeToFile(YearMonth yearMonth, JsonMonth record)
    {
        final Path file = dateToFileMapper.getPathForDate(yearMonth);
        LOG.info("Write month {} to file {}", yearMonth, file);
        createDirectory(file.getParent());
        try (OutputStream stream = Files.newOutputStream(file, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING))
        {
            jsonb.toJson(record, stream);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error writing file " + file, e);
        }
    }

    private void createDirectory(Path dir)
    {
        if (dir.toFile().isDirectory())
        {
            return;
        }
        try
        {
            Files.createDirectories(dir);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error creating dir " + dir, e);
        }
    }

    List<YearMonth> getAvailableDataYearMonth()
    {
        return dateToFileMapper.getAllYearMonths().sorted().collect(toList());
    }

    List<JsonMonth> loadAll()
    {
        return dateToFileMapper.getAllFiles()
                .map(this::loadFromFile)
                .sorted(comparing(JsonMonth::getYear).thenComparing(JsonMonth::getMonth))
                .collect(toList());
    }
}
