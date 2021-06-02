package org.itsallcode.whiterabbit.logic.storage;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;
import org.itsallcode.whiterabbit.logic.service.HolidayService;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

class MonthIndexStorage implements Storage
{
    private static final Logger LOG = LogManager.getLogger(MonthIndexStorage.class);

    private final ContractTermsService contractTerms;
    private final ProjectService projectService;
    private final MonthDataStorage fileStorage;
    private final HolidayService holidayService;

    MonthIndexStorage(ContractTermsService contractTerms, ProjectService projectService,
            MonthDataStorage fileStorage, HolidayService holidayService)
    {
        this.contractTerms = contractTerms;
        this.projectService = projectService;
        this.fileStorage = fileStorage;
        this.holidayService = holidayService;
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
        final ModelFactory factory = fileStorage.getModelFactory();
        final MonthData month = factory.createMonthData();
        month.setYear(date.getYear());
        month.setMonth(date.getMonth());

        final ArrayList<DayData> list = new ArrayList<>();
        final DayData holiday = factory.createDayData();
        // need a service to inquire all holidays for given month and year
        holiday.setDate(LocalDate.of(2021, 6, 2));
        holiday.setType(DayType.HOLIDAY);
        holiday.setComment("Fronleichnam");
        list.add(holiday);
        month.setDays(list);
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
