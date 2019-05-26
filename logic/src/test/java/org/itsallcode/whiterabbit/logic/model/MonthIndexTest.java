package org.itsallcode.whiterabbit.logic.model;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.junit.jupiter.api.Test;

class MonthIndexTest
{
    @Test
    void testCalculateThisMonthOvertimeNoDays()
    {
        assertThat(create(Duration.ZERO)).isEqualTo(Duration.ZERO);
    }

    @Test
    void testCalculateThisMonthIgnoresPreviousMonths()
    {
        assertThat(create(Duration.ofHours(1))).isEqualTo(Duration.ZERO);
    }

    @Test
    void testCalculateThisMonthSingleDayPositiveOvertime()
    {
        assertThat(create(day(Duration.ofMinutes(10), 1))).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    void testCalculateThisMonthSingleDayNegativeOvertime()
    {
        assertThat(create(day(Duration.ofMinutes(10).negated(), 1)))
                .isEqualTo(Duration.ofMinutes(10).negated());
    }

    @Test
    void testCalculateThisMonthMultipleDaysPositiveOvertime()
    {
        assertThat(create(day(Duration.ofMinutes(10), 1), day(Duration.ofMinutes(10), 2),
                day(Duration.ofMinutes(10), 3))).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    void testCalculateThisMonthMultipleDaysNegativeOvertime()
    {
        assertThat(create(day(Duration.ofMinutes(10), 1), day(Duration.ofMinutes(10), 2),
                day(Duration.ofMinutes(30).negated(), 3)))
                        .isEqualTo(Duration.ofMinutes(10).negated());
    }

    private Duration create(JsonDay... days)
    {
        return create(null, days);
    }

    private Duration create(Duration overtimePreviousMonth, JsonDay... days)
    {
        return MonthIndex.create(jsonMonth(overtimePreviousMonth, days))
                .calculateThisMonthOvertime();
    }

    private JsonMonth jsonMonth(Duration overtimePreviousMonth, JsonDay... days)
    {
        final JsonMonth month = new JsonMonth();
        month.setYear(2019);
        month.setMonth(Month.MAY);
        month.setDays(asList(days));
        month.setOvertimePreviousMonth(
                overtimePreviousMonth != null ? overtimePreviousMonth : Duration.ZERO);
        return month;
    }

    private JsonDay day(Duration overtime, int dayOfMonth)
    {
        final LocalTime begin = LocalTime.of(8, 0);
        final LocalTime end = begin.plus(Duration.ofHours(8))
                .plus(Duration.ofMinutes(45).plus(overtime));

        final JsonDay day = new JsonDay();
        day.setDate(LocalDate.of(2019, Month.MAY, dayOfMonth));
        day.setBegin(begin);
        day.setEnd(end);
        day.setType(DayType.WORK);
        return day;
    }
}
