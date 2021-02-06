package org.itsallcode.whiterabbit.logic.model;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;

import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.test.TestingConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MonthIndexTest
{
    @Mock
    private ProjectService projectServiceMock;

    @Test
    void testCalculateTotalOvertimeOvertimeNoDays()
    {
        assertThat(calculateTotalOvertime(Duration.ZERO)).isEqualTo(Duration.ZERO);
    }

    @Test
    void testCalculateTotalOvertimeConsidersPreviousMonth()
    {
        assertThat(calculateTotalOvertime(Duration.ofHours(1))).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void testCalculateTotalOvertimeConsidersPreviousMonthAndCurrentMonth()
    {
        assertThat(calculateTotalOvertime(Duration.ofHours(1), day(Duration.ofMinutes(10), 1)))
                .isEqualTo(Duration.ofHours(1).plusMinutes(10));
    }

    @Test
    void testCalculateTotalOvertimeSingleDayPositiveOvertime()
    {
        assertThat(calculateTotalOvertime(day(Duration.ofMinutes(10), 1))).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    void testCalculateTotalOvertimeSingleDayNegativeOvertime()
    {
        assertThat(calculateTotalOvertime(day(Duration.ofMinutes(10).negated(), 1)))
                .isEqualTo(Duration.ofMinutes(10).negated());
    }

    @Test
    void testCalculateTotalOvertimeMultipleDaysPositiveOvertime()
    {
        assertThat(calculateTotalOvertime(day(Duration.ofMinutes(10), 1), day(Duration.ofMinutes(10), 2),
                day(Duration.ofMinutes(10), 3))).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    void testCalculateTotalOvertimeMultipleDaysNegativeOvertime()
    {
        assertThat(calculateTotalOvertime(day(Duration.ofMinutes(10), 1), day(Duration.ofMinutes(10), 2),
                day(Duration.ofMinutes(30).negated(), 3))).isEqualTo(Duration.ofMinutes(10).negated());
    }

    @Test
    void testSetOvertimePreviousMonthUpdatesJsonRecord()
    {
        final JsonMonth jsonMonth = jsonMonth(Duration.ofHours(1));
        final MonthIndex monthIndex = create(jsonMonth);
        monthIndex.setOvertimePreviousMonth(Duration.ofHours(2));

        assertThat(jsonMonth.getOvertimePreviousMonth()).isEqualTo(Duration.ofHours(2));
    }

    @Test
    void testSetOvertimePreviousMonthUpdatesTotalOvertime()
    {
        final JsonMonth jsonMonth = jsonMonth(Duration.ofHours(1));
        final MonthIndex monthIndex = create(jsonMonth);

        assertThat(monthIndex.getTotalOvertime()).isEqualTo(Duration.ofHours(1));

        monthIndex.setOvertimePreviousMonth(Duration.ofHours(2));
        assertThat(monthIndex.getTotalOvertime()).isEqualTo(Duration.ofHours(2));

        assertThat(jsonMonth.getOvertimePreviousMonth()).isEqualTo(Duration.ofHours(2));
    }

    @Test
    void gettingNewDayReturnsEmptyDay()
    {
        final LocalDate date = LocalDate.of(2020, 5, 4);
        final MonthIndex monthIndex = create(TestingConfig.builder().build(), jsonMonth(YearMonth.from(date), null));

        final DayRecord day = monthIndex.getDay(date);

        assertThat(day.getDate()).isEqualTo(date);
        assertThat(day.getBegin()).isNull();
        assertThat(day.getEnd()).isNull();
        assertThat(day.getCustomWorkingTime()).isEmpty();
        assertThat(day.getComment()).isNull();
        assertThat(day.getInterruption()).isZero();
        assertThat(day.getMandatoryBreak()).isZero();
        assertThat(day.getMandatoryWorkingTime()).isZero();
        assertThat(day.getOverallOvertime()).isZero();
        assertThat(day.getOvertime()).isZero();
        assertThat(day.getPreviousDayOvertime()).isZero();
        assertThat(day.getRawWorkingTime()).isZero();
        assertThat(day.getTotalOvertimeThisMonth()).isZero();
        assertThat(day.getType()).isEqualTo(DayType.WORK);
        assertThat(day.getWorkingTime()).isZero();
        assertThat(day.getMonth()).isSameAs(monthIndex);
    }

    @Test
    void gettingDaysDoesNotAddThemToJson()
    {
        final LocalDate date = LocalDate.of(2020, 5, 4);
        final JsonMonth jsonMonth = jsonMonth(YearMonth.from(date), null);
        final MonthIndex monthIndex = create(TestingConfig.builder().build(), jsonMonth);

        final DayRecord day = monthIndex.getDay(date);

        assertThat(day.getDate()).isEqualTo(date);

        assertThat(jsonMonth.getDays()).isEmpty();
    }

    @Test
    void newDaysHaveNoCustomWorkingTime()
    {
        final LocalDate date = LocalDate.of(2020, 5, 4);
        final MonthIndex monthIndex = create(TestingConfig.builder().withCurrentHoursPerDay(null).build(),
                jsonMonth(YearMonth.from(date), null));

        final DayRecord day = monthIndex.getDay(date);

        assertThat(day.getCustomWorkingTime()).isEmpty();
    }

    @Test
    void newDaysHaveCustomWorkingTimeWhenCurrentHoursPerDayAreConfigured()
    {
        final LocalDate date = LocalDate.of(2020, 5, 4);
        final MonthIndex monthIndex = create(
                TestingConfig.builder().withCurrentHoursPerDay(Duration.ofHours(5)).build(),
                jsonMonth(YearMonth.from(date), null));

        final DayRecord day = monthIndex.getDay(date);

        assertThat(day.getCustomWorkingTime()).isPresent().contains(Duration.ofHours(5));
    }

    private Duration calculateTotalOvertime(JsonDay... days)
    {
        return calculateTotalOvertime(null, days);
    }

    private Duration calculateTotalOvertime(Duration overtimePreviousMonth, JsonDay... days)
    {
        return create(jsonMonth(overtimePreviousMonth, days)).getTotalOvertime();
    }

    private JsonMonth jsonMonth(Duration overtimePreviousMonth, JsonDay... days)
    {
        return jsonMonth(YearMonth.of(2019, Month.MAY), overtimePreviousMonth, days);
    }

    private JsonMonth jsonMonth(YearMonth yearMonth, Duration overtimePreviousMonth, JsonDay... days)
    {
        final JsonMonth month = new JsonMonth();
        month.setYear(yearMonth.getYear());
        month.setMonth(yearMonth.getMonth());
        month.setDays(asList(days));
        month.setOvertimePreviousMonth(overtimePreviousMonth != null ? overtimePreviousMonth : Duration.ZERO);
        return month;
    }

    private JsonDay day(Duration overtime, int dayOfMonth)
    {
        final LocalTime begin = LocalTime.of(8, 0);
        final LocalTime end = begin.plus(Duration.ofHours(8)).plus(Duration.ofMinutes(45).plus(overtime));

        final JsonDay day = new JsonDay();
        day.setDate(LocalDate.of(2019, Month.MAY, dayOfMonth));
        day.setBegin(begin);
        day.setEnd(end);
        day.setType(DayType.WORK);
        return day;
    }

    private MonthIndex create(JsonMonth record)
    {
        return create(TestingConfig.builder().build(), record);
    }

    private MonthIndex create(Config config, JsonMonth record)
    {
        return MonthIndex.create(new ContractTermsService(config), projectServiceMock, record);
    }
}
