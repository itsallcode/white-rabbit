package org.itsallcode.whiterabbit.logic.storage;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.MonthDataStorage;
import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

class MonthIndexStorage implements Storage
{
    private static final Logger LOG = LogManager.getLogger(MonthIndexStorage.class);

    private final ContractTermsService contractTerms;
    private final ProjectService projectService;
    private final MonthDataStorage fileStorage;

    MonthIndexStorage(ContractTermsService contractTerms, ProjectService projectService,
            MonthDataStorage fileStorage)
    {
        this.contractTerms = contractTerms;
        this.projectService = projectService;
        this.fileStorage = fileStorage;
    }

    @Override
    public Optional<MonthIndex> loadMonth(YearMonth date)
    {
        return fileStorage.load(date).map(this::createMonthIndex);
    }

    @Override
    public MonthIndex loadOrCreate(final YearMonth yearMonth)
    {
        final Optional<MonthIndex> month = loadMonth(yearMonth);
        return month.orElseGet(() -> createNewMonth(yearMonth));
    }

    @Override
    public void storeMonth(MonthIndex month)
    {
        fileStorage.store(month.getYearMonth(), month.getMonthRecord());
    }

    @Override
    public MultiMonthIndex loadAll()
    {
        return new MultiMonthIndex(fileStorage.loadAll().stream()
                .map(this::createMonthIndex)
                .collect(toList()));
    }

    @Override
    public List<YearMonth> getAvailableDataMonths()
    {
        return fileStorage.getAvailableDataMonths();
    }

    private MonthIndex createNewMonth(YearMonth date)
    {
        final MonthData month = fileStorage.getModelFactory().createMonthData();
        month.setYear(date.getYear());
        month.setMonth(date.getMonth());
        month.setDays(new ArrayList<>());
        month.setOvertimePreviousMonth(loadPreviousMonthOvertime(date));
        return createMonthIndex(month);
    }

    private MonthIndex createMonthIndex(final MonthData jsonMonth)
    {
        return MonthIndex.create(contractTerms, projectService, fileStorage.getModelFactory(), jsonMonth);
    }

    Duration loadPreviousMonthOvertime(YearMonth date)
    {
        final YearMonth previousYearMonth = date.minus(1, ChronoUnit.MONTHS);
        final Duration overtime = loadMonth(previousYearMonth)
                .map(m -> m.getTotalOvertime().truncatedTo(ChronoUnit.MINUTES))
                .orElse(Duration.ZERO);
        LOG.info("Found overtime {} for previous month {}", overtime, previousYearMonth);
        return overtime;
    }
}
