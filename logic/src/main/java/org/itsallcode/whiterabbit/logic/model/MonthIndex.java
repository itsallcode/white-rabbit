package org.itsallcode.whiterabbit.logic.model;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

public class MonthIndex
{
    private final ModelFactory modelFactory;
    private final MonthData monthRecord;
    private final Map<LocalDate, DayRecord> days;

    private MonthIndex(ModelFactory modelFactory, MonthData monthRecord, Map<LocalDate, DayRecord> days)
    {
        this.modelFactory = Objects.requireNonNull(modelFactory, "modelFactory");
        this.monthRecord = monthRecord;
        this.days = days;
    }

    public static MonthIndex create(ContractTermsService contractTerms, ProjectService projectService,
            ModelFactory modelFactory, MonthData monthRecord)
    {
        final Map<LocalDate, DayData> jsonDays = monthRecord.getDays().stream()
                .collect(toMap(DayData::getDate, Function.identity()));
        final Map<LocalDate, DayRecord> days = new HashMap<>();
        final MonthIndex monthIndex = new MonthIndex(modelFactory, monthRecord, days);

        final YearMonth yearMonth = YearMonth.of(monthRecord.getYear(), monthRecord.getMonth());

        DayRecord previousDay = null;
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++)
        {
            final LocalDate date = yearMonth.atDay(day);
            final DayData jsonDay = jsonDays.computeIfAbsent(date, d -> createDummyDay(d, contractTerms, modelFactory));
            final DayRecord dayRecord = new DayRecord(contractTerms, jsonDay, previousDay, monthIndex, projectService,
                    modelFactory);
            days.put(dayRecord.getDate(), dayRecord);
            previousDay = dayRecord;
        }

        return monthIndex;
    }

    private static DayData createDummyDay(LocalDate date, ContractTermsService contractTerms, ModelFactory modelFactory)
    {
        final DayData day = modelFactory.createDayData();
        day.setDate(date);
        if (contractTerms.getContractedWorkingTimePerDay() != null
                && !contractTerms.getContractedWorkingTimePerDay().equals(contractTerms.getCurrentWorkingTimePerDay()))
        {
            day.setWorkingHours(contractTerms.getCurrentWorkingTimePerDay());
        }
        return day;
    }

    public YearMonth getYearMonth()
    {
        return YearMonth.of(monthRecord.getYear(), monthRecord.getMonth());
    }

    public DayRecord getDay(LocalDate date)
    {
        return days.get(date);
    }

    public void put(DayRecord day)
    {
        this.days.put(day.getDate(), day);
    }

    public MonthData getMonthRecord()
    {
        final List<DayData> sortedNonDummyJsonDays = getSortedDays() //
                .filter(d -> !d.isDummyDay()) //
                .map(DayRecord::getJsonDay) //
                .collect(toList());

        final MonthData month = modelFactory.createMonthData();
        month.setOvertimePreviousMonth(monthRecord.getOvertimePreviousMonth());
        month.setYear(monthRecord.getYear());
        month.setMonth(monthRecord.getMonth());
        month.setDays(sortedNonDummyJsonDays);
        return month;
    }

    public Duration getOvertimePreviousMonth()
    {
        return monthRecord.getOvertimePreviousMonth();
    }

    public Stream<DayRecord> getSortedDays()
    {
        return days.values().stream() //
                .sorted(Comparator.comparing(DayRecord::getDate));
    }

    public void setOvertimePreviousMonth(Duration overtimePreviousMonth)
    {
        monthRecord.setOvertimePreviousMonth(overtimePreviousMonth);
    }

    public Duration getTotalOvertime()
    {
        return getOvertimePreviousMonth().plus(getThisMonthOvertime());
    }

    private Duration getThisMonthOvertime()
    {
        final Optional<DayRecord> lastDay = getSortedDays().reduce((first, second) -> second);
        return lastDay.map(DayRecord::getTotalOvertimeThisMonth) //
                .orElse(Duration.ZERO);
    }

    public int getVacationDayCount()
    {
        return getVacationDays().size();
    }

    public List<LocalDate> getVacationDays()
    {
        return monthRecord.getDays().stream()
                .filter(day -> day.getType() == DayType.VACATION)
                .map(DayData::getDate)
                .collect(toList());
    }
}
