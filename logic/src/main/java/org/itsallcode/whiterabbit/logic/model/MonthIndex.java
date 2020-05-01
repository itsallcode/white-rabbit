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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;

public class MonthIndex
{
    private final JsonMonth record;
    private final Map<LocalDate, DayRecord> days;

    private MonthIndex(JsonMonth record, Map<LocalDate, DayRecord> days)
    {
        this.record = record;
        this.days = days;
    }

    public static MonthIndex create(ContractTermsService contractTerms, JsonMonth record)
    {
        final Map<LocalDate, JsonDay> jsonDays = record.getDays().stream()
                .collect(toMap(JsonDay::getDate, Function.identity()));
        final Map<LocalDate, DayRecord> days = new HashMap<>();
        final MonthIndex monthIndex = new MonthIndex(record, days);

        final YearMonth yearMonth = YearMonth.of(record.getYear(), record.getMonth());

        DayRecord previousDay = null;
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++)
        {
            final LocalDate date = yearMonth.atDay(day);
            final JsonDay jsonDay = jsonDays.computeIfAbsent(date, d -> createDummyDay(d, contractTerms));
            final DayRecord dayRecord = new DayRecord(contractTerms, jsonDay, previousDay, monthIndex);
            days.put(dayRecord.getDate(), dayRecord);
            previousDay = dayRecord;
        }

        return monthIndex;
    }

    private static JsonDay createDummyDay(LocalDate date, ContractTermsService contractTerms)
    {
        final JsonDay day = new JsonDay();
        day.setDate(date);
        if (!contractTerms.getContractedWorkingTimePerDay().equals(contractTerms.getCurrentWorkingTimePerDay()))
        {
            day.setWorkingHours(contractTerms.getCurrentWorkingTimePerDay());
        }
        return day;
    }

    public YearMonth getYearMonth()
    {
        return YearMonth.of(record.getYear(), record.getMonth());
    }

    public DayRecord getDay(LocalDate date)
    {
        return days.get(date);
    }

    public void put(DayRecord day)
    {
        this.days.put(day.getDate(), day);
    }

    public JsonMonth getMonthRecord()
    {
        final List<JsonDay> sortedNonDummyJsonDays = getSortedDays() //
                .filter(d -> !d.isDummyDay()) //
                .map(DayRecord::getJsonDay) //
                .collect(toList());
        return JsonMonth.create(record, sortedNonDummyJsonDays);
    }

    public Duration getOvertimePreviousMonth()
    {
        return record.getOvertimePreviousMonth();
    }

    public Stream<DayRecord> getSortedDays()
    {
        return days.values().stream() //
                .sorted(Comparator.comparing(DayRecord::getDate));
    }

    public void setOvertimePreviousMonth(Duration overtimePreviousMonth)
    {
        record.setOvertimePreviousMonth(overtimePreviousMonth);
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
        return (int) record.getDays().stream() //
                .filter(day -> day.getType() == DayType.VACATION) //
                .count();
    }
}
