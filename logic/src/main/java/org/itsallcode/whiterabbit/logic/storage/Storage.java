package org.itsallcode.whiterabbit.logic.storage;

import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.time.Duration;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

public class Storage
{
    private static final Logger LOG = LogManager.getLogger(Storage.class);

    private final ContractTermsService contractTerms;
    private final ProjectService projectService;
    private final JsonFileStorage fileStorage;

    Storage(ContractTermsService contractTerms, ProjectService projectService, JsonFileStorage fileStorage)
    {
        this.contractTerms = contractTerms;
        this.projectService = projectService;
        this.fileStorage = fileStorage;
    }

    public static Storage create(Path dataDir, ContractTermsService contractTerms,
            ProjectService projectService)
    {
        final DateToFileMapper dateToFileMapper = new DateToFileMapper(dataDir);
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return new Storage(contractTerms, projectService, new JsonFileStorage(jsonb, dateToFileMapper));
    }

    public Optional<MonthIndex> loadMonth(YearMonth date)
    {
        return fileStorage.loadMonthRecord(date).map(this::createMonthIndex);
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
        return createMonthIndex(month);
    }

    private MonthIndex createMonthIndex(final JsonMonth month)
    {
        return MonthIndex.create(contractTerms, month, projectService);
    }

    public void storeMonth(MonthIndex month)
    {
        fileStorage.writeToFile(month.getYearMonth(), month.getMonthRecord());
    }

    public MultiMonthIndex loadAll()
    {
        return new MultiMonthIndex(fileStorage.loadAll().stream()
                .map(this::createMonthIndex)
                .collect(toList()));
    }

    public Duration loadPreviousMonthOvertime(YearMonth date)
    {
        final YearMonth previousYearMonth = date.minus(1, ChronoUnit.MONTHS);
        final Duration overtime = loadMonth(previousYearMonth)
                .map(m -> m.getTotalOvertime().truncatedTo(ChronoUnit.MINUTES))
                .orElse(Duration.ZERO);
        LOG.info("Found overtime {} for previous month {}", overtime, previousYearMonth);
        return overtime;
    }

    public List<YearMonth> getAvailableDataYearMonth()
    {
        return fileStorage.getAvailableDataYearMonth();
    }
}
