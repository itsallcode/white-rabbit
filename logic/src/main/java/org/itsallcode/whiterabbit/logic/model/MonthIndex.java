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
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;

public class MonthIndex
{
    private static final Logger LOG = LogManager.getLogger(MonthIndex.class);

    private final JsonMonth record;
    private final Map<LocalDate, DayRecord> days;
    private final Duration totalOvertime;

    private MonthIndex(JsonMonth record, Map<LocalDate, DayRecord> days, Duration totalOvertime)
    {
        this.record = record;
        this.days = days;
        this.totalOvertime = totalOvertime;
    }

    public static MonthIndex create(JsonMonth record)
    {
        Duration currentOvertime = getOvertimePreviousMonth(record);
        final Map<LocalDate, DayRecord> days = new HashMap<>();
        final Map<LocalDate, JsonDay> jsonDays = record.getDays().stream()
                .collect(toMap(JsonDay::getDate, Function.identity()));
        final MonthIndex monthIndex = new MonthIndex(record, days, currentOvertime);

        final YearMonth yearMonth = YearMonth.of(record.getYear(), record.getMonth());
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++)
        {
            final LocalDate date = yearMonth.atDay(day);
            final JsonDay jsonDay = jsonDays.computeIfAbsent(date, MonthIndex::createDummyDay);
            final DayRecord dayRecord = new DayRecord(jsonDay, currentOvertime, monthIndex);
            if (!dayRecord.isDummyDay())
            {
                currentOvertime = dayRecord.getTotalOvertime();
            }
            days.put(dayRecord.getDate(), dayRecord);
        }

        return monthIndex;
    }

    private static JsonDay createDummyDay(LocalDate date)
    {
        final JsonDay day = new JsonDay();
        LOG.trace("No entry found for {}: create dummy day", date);
        day.setDate(date);
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
                .peek(System.out::println).map(DayRecord::getJsonDay) //
                .collect(toList());
        return JsonMonth.create(record, sortedNonDummyJsonDays);
    }

    public Duration getTotalOvertime()
    {
        return totalOvertime;
    }

    public Duration calculateThisMonthOvertime()
    {
        final long overtimeMinutes = days.values().stream() //
                .filter(day -> !day.isDummyDay()) //
                .peek(System.out::println) //
                .map(DayRecord::getOvertime) //
                .mapToLong(Duration::toMinutes).sum();
        return Duration.ofMinutes(overtimeMinutes);
    }

    public Stream<DayRecord> getSortedDays()
    {
        return days.values().stream() //
                .sorted(Comparator.comparing(DayRecord::getDate));
    }

    private static Duration getOvertimePreviousMonth(JsonMonth month)
    {
        if (month.getOvertimePreviousMonth() != null)
        {
            return month.getOvertimePreviousMonth();
        }
        else
        {
            LOG.warn("No overtime for previous month found for {} / {}", month.getMonth(),
                    month.getYear());
            return Duration.ZERO;
        }
    }

    public Duration getOvertimePreviousMonth()
    {
        return record.getOvertimePreviousMonth();
    }

    public void setOvertimePreviousMonth(Duration overtimePreviousMonth)
    {
        record.setOvertimePreviousMonth(overtimePreviousMonth);
    }
}
