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
import java.util.Optional;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;

public class Storage
{
    private static final Logger LOG = LogManager.getLogger(Storage.class);

    private final Jsonb jsonb;
    private final DateToFileMapper dateToFileMapper;

    private final ContractTermsService contractTerms;

    private Storage(DateToFileMapper dateToFileMapper, ContractTermsService contractTerms, Jsonb jsonb)
    {
        this.dateToFileMapper = dateToFileMapper;
        this.contractTerms = contractTerms;
        this.jsonb = jsonb;
    }

    public Storage(DateToFileMapper dateToFileMapper, ContractTermsService contractTerms)
    {
        this(dateToFileMapper, contractTerms, JsonbBuilder.create(new JsonbConfig().withFormatting(true)));
    }

    public Optional<MonthIndex> loadMonth(YearMonth date)
    {
        return loadMonthRecord(date).map(month -> MonthIndex.create(contractTerms, month));
    }

    public MonthIndex loadOrCreate(final YearMonth yearMonth)
    {
        final Optional<MonthIndex> month = loadMonth(yearMonth);
        return month.orElseGet(() -> createNewMonth(yearMonth));
    }

    private MonthIndex createNewMonth(YearMonth date)
    {
        final JsonMonth month = new JsonMonth();
        month.setYear(date.getYear());
        month.setMonth(date.getMonth());
        month.setDays(new ArrayList<>());
        month.setOvertimePreviousMonth(loadPreviousMonthOvertime(date));
        return MonthIndex.create(contractTerms, month);
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
            final MonthIndex month = MonthIndex.create(contractTerms, jsonMonth);
            months.add(month);
        }

        months.sort(Comparator.comparing(MonthIndex::getYearMonth));

        return new MultiMonthIndex(months);
    }

    private void writeToFile(MonthIndex month)
    {
        final Path file = dateToFileMapper.getPathForDate(month.getYearMonth());
        LOG.info("Write month {} to file {}", month.getYearMonth(), file);
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

    private Optional<JsonMonth> loadMonthRecord(YearMonth date)
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

    public Duration loadPreviousMonthOvertime(YearMonth date)
    {
        final YearMonth previousYearMonth = date.minus(1, ChronoUnit.MONTHS);
        final Duration overtime = loadMonth(previousYearMonth) //
                .map(m -> m.getTotalOvertime().truncatedTo(ChronoUnit.MINUTES)) //
                .orElse(Duration.ZERO);
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
