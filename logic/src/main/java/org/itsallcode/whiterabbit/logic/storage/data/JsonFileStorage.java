package org.itsallcode.whiterabbit.logic.storage.data;

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
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.MonthDataStorage;
import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;

public class JsonFileStorage implements MonthDataStorage
{
    private static final Logger LOG = LogManager.getLogger(JsonFileStorage.class);

    private final Jsonb jsonb;
    private final DateToFileMapper dateToFileMapper;

    JsonFileStorage(Jsonb jsonb, DateToFileMapper dateToFileMapper)
    {
        this.jsonb = jsonb;
        this.dateToFileMapper = dateToFileMapper;
    }

    public static MonthDataStorage create(Path dataDir)
    {
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return new JsonFileStorage(jsonb, new DateToFileMapper(dataDir));
    }

    @Override
    public Optional<MonthData> load(YearMonth date)
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

    @Override
    public void store(YearMonth yearMonth, MonthData record)
    {
        final Path file = dateToFileMapper.getPathForDate(yearMonth);
        LOG.trace("Write month {} to file {}", yearMonth, file);
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

    @Override
    public List<YearMonth> getAvailableDataMonths()
    {
        return dateToFileMapper.getAllYearMonths().sorted().collect(toList());
    }

    @Override
    public List<MonthData> loadAll()
    {
        return dateToFileMapper.getAllFiles()
                .filter(file -> !file.getFileName().toString().equals(Config.PROJECTS_JSON))
                .map(this::loadFromFile)
                .sorted(comparing(JsonMonth::getYear).thenComparing(JsonMonth::getMonth))
                .collect(toList());
    }
}
