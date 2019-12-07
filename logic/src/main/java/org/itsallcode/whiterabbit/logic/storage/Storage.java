package org.itsallcode.whiterabbit.logic.storage;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;

public class Storage
{
    private static final Logger LOG = LogManager.getLogger(Storage.class);

    private final Jsonb jsonb;
    private final DateToFileMapper dateToFileMapper;

    private Storage(DateToFileMapper dateToFileMapper, Jsonb jsonb)
    {
        this.dateToFileMapper = dateToFileMapper;
        this.jsonb = jsonb;
    }

    public Storage(DateToFileMapper dateToFileMapper)
    {
        this(dateToFileMapper, JsonbBuilder.create(new JsonbConfig().withFormatting(true)));
    }

    public MonthIndex loadMonth(YearMonth date)
    {
        return MonthIndex.create(loadMonthRecord(date));
    }

    public void storeMonth(MonthIndex month)
    {
        writeToFile(month);
    }

    public MultiMonthIndex loadAll()
    {
        final List<MonthIndex> months = new ArrayList<>();
        for (final Path file : dateToFileMapper.getAllFiles().collect(toList()))
        {
            final JsonMonth jsonMonth = loadFromFile(file);
            final MonthIndex month = MonthIndex.create(jsonMonth);
            months.add(month);
        }

        Collections.sort(months, Comparator.comparing(MonthIndex::getYearMonth));

        return new MultiMonthIndex(months);
    }

    private void writeToFile(MonthIndex month)
    {
        final Path file = dateToFileMapper.getPathForDate(month.getYearMonth());
        LOG.trace("Write month {} to file {}", month.getYearMonth(), file);
        createDirectory(file.getParent());
        try (OutputStream stream = Files.newOutputStream(file, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING))
        {
            jsonb.toJson(month.getMonthRecord(), stream);
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

    private JsonMonth loadMonthRecord(YearMonth date)
    {
        final Path file = dateToFileMapper.getPathForDate(date);
        if (file.toFile().exists())
        {
            LOG.trace("Found file {} for month {}", file, date);
            return loadFromFile(file);
        }
        LOG.debug("File {} not found for month {}: create new month", file, date);
        return createNewMonth(date);
    }

    private JsonMonth createNewMonth(YearMonth date)
    {
        final JsonMonth month = new JsonMonth();
        month.setYear(date.getYear());
        month.setMonth(date.getMonth());
        month.setDays(new ArrayList<>());
        month.setOvertimePreviousMonth(loadPreviousMonthOvertime(date));
        return month;
    }

    private Duration loadPreviousMonthOvertime(YearMonth date)
    {
        final YearMonth previousYearMonth = date.minus(1, ChronoUnit.MONTHS);
        final MonthIndex previousMonth = loadMonth(previousYearMonth);
        final Duration overtime = previousMonth.getTotalOvertime().truncatedTo(ChronoUnit.MINUTES);
        LOG.info("Found overtime {} for previous month {}", overtime, previousYearMonth);
        return overtime;
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

    public List<YearMonth> getAvailableDataYearMonth()
    {
        return dateToFileMapper.getAllYearMonths().sorted().collect(toList());
    }
}
