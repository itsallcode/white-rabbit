package org.itsallcode.whiterabbit.logic.model;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;

public class MonthIndex
{
    private final JsonMonth record;
    private final Map<LocalDate, DayRecord> days;
    private final Duration totalOvertime;

    private MonthIndex(JsonMonth record, Map<LocalDate, DayRecord> days, Duration totalOvertime)
    {
        this.record = record;
        this.days = days;
        this.totalOvertime = totalOvertime;
    }

    public static MonthIndex create(JsonMonth record, Duration previousOvertime)
    {
        Duration currentOvertime = previousOvertime;
        final Map<LocalDate, DayRecord> days = new HashMap<>();
        for (final JsonDay jsonDay : record.getDays())
        {
            final DayRecord day = new DayRecord(jsonDay, currentOvertime);
            currentOvertime = day.getTotalOvertime();
            days.put(day.getDate(), day);
        }

        return new MonthIndex(record, days, currentOvertime);
    }

    public YearMonth getYearMonth()
    {
        return YearMonth.of(record.getYear(), record.getMonth());
    }

    public DayRecord getDay(LocalDate date)
    {
        return days.computeIfAbsent(date, this::createDay);
    }

    public void put(DayRecord day)
    {
        this.days.put(day.getDate(), day);
    }

    public JsonMonth getMonthRecord()
    {
        return JsonMonth.create(record, getSortedJsonDays());
    }

    public Duration getTotalOvertime()
    {
        return totalOvertime;
    }

    private List<JsonDay> getSortedJsonDays()
    {
        return getSortedDays() //
                .map(DayRecord::getJsonDay) //
                .collect(toList());
    }

    public Stream<DayRecord> getSortedDays()
    {
        return days.values().stream() //
                .sorted(Comparator.comparing(DayRecord::getDate));
    }

    private DayRecord createDay(LocalDate date)
    {
        final JsonDay day = new JsonDay();
        day.setDate(date);
        return new DayRecord(day, Duration.ZERO);
    }
}
