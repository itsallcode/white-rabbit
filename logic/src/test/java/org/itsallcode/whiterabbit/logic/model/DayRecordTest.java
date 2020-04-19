package org.itsallcode.whiterabbit.logic.model;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.junit.jupiter.api.Test;

class DayRecordTest
{
    @Test
    void mandatoryWorkingTimeIsZeroOnWeekend()
    {
        assertMandatoryWorkingTime(LocalDate.of(2020, 4, 19), LocalTime.of(8, 0), LocalTime.of(14, 0), Duration.ZERO);
    }

    @Test
    void mandatoryWorkingTimeIs8HoursOnWorkingDay()
    {
        assertMandatoryWorkingTime(LocalDate.of(2020, 4, 20), LocalTime.of(8, 0), LocalTime.of(14, 0),
                Duration.ofHours(8));
    }

    @Test
    void mandatoryWorkingTimeIsZeroForDummyDayOnWeekendDay()
    {
        final DayRecord day = createDummyDay(LocalDate.of(2020, 4, 19));
        assertThat(day.getMandatoryWorkingTime()).as("mandatory working time").isEqualTo(Duration.ZERO);
    }

    @Test
    void mandatoryWorkingTimeIsZeroForDummyDayOnWorkingDay()
    {
        final DayRecord day = createDummyDay(LocalDate.of(2020, 4, 20));
        assertThat(day.getMandatoryWorkingTime()).as("mandatory working time").isEqualTo(Duration.ZERO);
    }

    @Test
    void testOnWorkingDayNoMandatoryBreakFor6h()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(14, 0), Duration.ZERO);
    }

    @Test
    void testOnNonWorkingDayNoMandatoryBreakFor8h()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 6), LocalTime.of(8, 0), LocalTime.of(16, 0), Duration.ZERO);
    }

    @Test
    void testOnWorkingDay45minMandatoryBreakLongerThan6hours()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(14, 1),
                Duration.ofMinutes(45));
    }

    @Test
    void testWorkingTimeOnWorkingDayNullBeginAndEnd()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), null, null, Duration.ZERO);
    }

    @Test
    void testWorkingTimeIsZeroOnWeekend()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 6), LocalTime.of(8, 0), LocalTime.of(14, 1), Duration.ZERO);
    }

    @Test
    void testWorkingTimeReturnsZeroForMissingEndTime()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), null, Duration.ZERO);
    }

    @Test
    void testWorkingTimeThrowsExceptionForMissingBeginTime()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), null, LocalTime.of(16, 0), Duration.ZERO);
    }

    @Test
    void testWorkingTime1h()
    {
        final DayRecord day = createDay(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(9, 0), null, null);
        assertThat(day.getWorkingTime()).isEqualTo(Duration.ofMinutes(60));
        assertThat(day.getMandatoryBreak()).isEqualTo(Duration.ZERO);
        assertThat(day.getOvertime()).isEqualTo(Duration.ofHours(-7));
    }

    @Test
    void testMondayIsNoWorkingDay()
    {
        assertWorkingDay(LocalDate.of(2018, 10, 1), true);
    }

    @Test
    void testSaturdayIsNoWorkingDay()
    {
        assertWorkingDay(LocalDate.of(2018, 10, 6), false);
    }

    @Test
    void testSundayIsNoWorkingDay()
    {
        assertWorkingDay(LocalDate.of(2018, 10, 7), false);
    }

    @Test
    void testMondayIsWorkdayType()
    {
        assertType(LocalDate.of(2018, 10, 1), DayType.WORK);
    }

    @Test
    void testSaturdayIsWeekendType()
    {
        assertType(LocalDate.of(2018, 10, 6), DayType.WEEKEND);
    }

    @Test
    void testSundayIsWeekendType()
    {
        assertType(LocalDate.of(2018, 10, 7), DayType.WEEKEND);
    }

    @Test
    void testDayWithTypeSetReturnsType()
    {
        assertDayType(createDay(null, null, null, DayType.HOLIDAY, null), DayType.HOLIDAY);
    }

    @Test
    void testNegativeOvertimeOnWorkingDay()
    {
        assertOvertime(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(9, 0), Duration.ofHours(-7));
    }

    @Test
    void testZeroOvertimeOnWorkingDay()
    {
        assertOvertime(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(16, 30), Duration.ofMinutes(-15));
    }

    @Test
    void testPositiveOvertimeOnWorkingDay()
    {
        assertOvertime(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(17, 0), Duration.ofMinutes(15));
    }

    @Test
    void testPositiveOvertimeOnWeekendDay()
    {
        assertOvertime(LocalDate.of(2018, 10, 6), LocalTime.of(8, 0), LocalTime.of(9, 0), Duration.ofHours(1));
    }

    @Test
    void testPositiveOvertimeOnWeekendDayWithoutBreak()
    {
        assertOvertime(LocalDate.of(2018, 10, 6), LocalTime.of(8, 0), LocalTime.of(16, 30),
                Duration.ofHours(8).plusMinutes(30));
    }

    @Test
    void totalOvertimeThisMonthForNullPreviousDay()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22), LocalTime.of(8, 0), LocalTime.of(16, 30));
        assertThat(day.getTotalOvertimeThisMonth()).as("total overtime this month").isEqualTo(Duration.ofMinutes(-15));
    }

    @Test
    void totalOvertimeThisMonthWithPreviousDayCancelsOut()
    {
        final LocalDate date = LocalDate.of(2020, 4, 22);
        final DayRecord previousDay = createDay(date.minusDays(1), LocalTime.of(8, 0), LocalTime.of(17, 0));
        final DayRecord day = createDay(date, LocalTime.of(8, 0), LocalTime.of(16, 30), null, null, previousDay);
        assertThat(day.getTotalOvertimeThisMonth()).as("total overtime this month").isEqualTo(Duration.ZERO);
    }

    @Test
    void totalOvertimeThisMonthWithPreviousDayIsGreaterThanZero()
    {
        final LocalDate date = LocalDate.of(2020, 4, 22);
        final DayRecord previousDay = createDay(date.minusDays(1), LocalTime.of(8, 0), LocalTime.of(17, 0));
        final DayRecord day = createDay(date, LocalTime.of(8, 0), LocalTime.of(16, 45), null, null, previousDay);
        assertThat(day.getTotalOvertimeThisMonth()).as("total overtime this month").isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void totalOvertimeThisMonthWithPreviousDayIsLessThanZero()
    {
        final LocalDate date = LocalDate.of(2020, 4, 22);
        final DayRecord previousDay = createDay(date.minusDays(1), LocalTime.of(8, 0), LocalTime.of(17, 0));
        final DayRecord day = createDay(date, LocalTime.of(8, 0), LocalTime.of(16, 15), null, null, previousDay);
        assertThat(day.getTotalOvertimeThisMonth()).as("total overtime this month").isEqualTo(Duration.ofMinutes(-15));
    }

    @Test
    void totalOverallOvertimeWithNullMonth()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22), LocalTime.of(8, 0), LocalTime.of(16, 15));
        assertThat(day.getOverallOvertime()).as("overall overtime").isEqualTo(Duration.ofMinutes(-30));
    }

    @Test
    void totalOverallOvertimeWithNullOvertimeFromPreviousMonth()
    {
        final LocalDate date = LocalDate.of(2020, 4, 22);
        final DayRecord day = createDay(date, LocalTime.of(8, 0), LocalTime.of(16, 15), null, null, null,
                month(date, null));
        assertThat(day.getOverallOvertime()).as("overall overtime").isEqualTo(Duration.ofMinutes(-30));
    }

    @Test
    void totalOverallOvertimeWithZeroOvertimeFromPreviousMonth()
    {
        final LocalDate date = LocalDate.of(2020, 4, 22);
        final DayRecord day = createDay(date, LocalTime.of(8, 0), LocalTime.of(16, 15), null, null, null,
                month(date, Duration.ZERO));
        assertThat(day.getOverallOvertime()).as("overall overtime").isEqualTo(Duration.ofMinutes(-30));
    }

    @Test
    void totalOverallOvertimeWithOvertimeFromPreviousMonth()
    {
        final LocalDate date = LocalDate.of(2020, 4, 22);
        final DayRecord day = createDay(date, LocalTime.of(8, 0), LocalTime.of(16, 15), null, null, null,
                month(date, Duration.ofMinutes(45)));
        assertThat(day.getOverallOvertime()).as("overall overtime").isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void testDefaultInterruptionIsZero()
    {
        final DayRecord day = createDay(null, null, null, null, null);
        assertThat(day.getInterruption()).isEqualTo(Duration.ZERO);
    }

    @Test
    void testInterruptionIsReturned()
    {
        final DayRecord day = createDay(null, null, null, null, Duration.ofMinutes(30));
        assertThat(day.getInterruption()).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    void setBeginFailsForNullValue()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        assertThatThrownBy(() -> day.setBegin(null)).isInstanceOf(NullPointerException.class).hasMessage("begin");
    }

    @Test
    void setBeginUpdatesRecord()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        day.setBegin(LocalTime.of(22, 33));
        assertThat(day.getBegin()).isEqualTo(LocalTime.of(22, 33));
    }

    @Test
    void setEndFailsForNullValue()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        assertThatThrownBy(() -> day.setEnd(null)).isInstanceOf(NullPointerException.class).hasMessage("end");
    }

    @Test
    void setEndUpdatesRecord()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        day.setEnd(LocalTime.of(22, 33));
        assertThat(day.getEnd()).isEqualTo(LocalTime.of(22, 33));
    }

    @Test
    void setInterruptionFailsForNullValue()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        assertThatThrownBy(() -> day.setInterruption(null)).isInstanceOf(NullPointerException.class)
                .hasMessage("interruption");
    }

    @Test
    void setInterruptionUpdatesRecord()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        day.setInterruption(Duration.ofMinutes(10));
        assertThat(day.getInterruption()).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    void setCommendUpdatesRecord()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        day.setComment("comment");
        assertThat(day.getComment()).isEqualTo("comment");
    }

    @Test
    void setTypeFailsForNullValue()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        assertThatThrownBy(() -> day.setType(null)).isInstanceOf(NullPointerException.class).hasMessage("type");
    }

    @Test
    void setTypeUpdatesRecord()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        day.setType(DayType.HOLIDAY);
        assertThat(day.getType()).isEqualTo(DayType.HOLIDAY);
    }

    @Test
    void getMonthReturnsMonth()
    {
        final LocalDate date = LocalDate.of(2020, 4, 22);
        final MonthIndex month = month(date, null);
        final DayRecord day = createDay(date, null, null, null, null, null, month);
        assertThat(day.getMonth()).isSameAs(month);
    }

    @Test
    void getJsonDayReturnsMonth()
    {
        final JsonDay jsonDay = new JsonDay();
        final DayRecord day = new DayRecord(null, jsonDay, null, null);
        assertThat(day.getJsonDay()).isSameAs(jsonDay);
    }

    private MonthIndex month(LocalDate date, Duration overtimePreviousMonth, JsonDay... days)
    {
        final JsonMonth jsonMonth = new JsonMonth();
        jsonMonth.setDays(asList(days));
        jsonMonth.setMonth(date.getMonth());
        jsonMonth.setYear(date.getYear());
        jsonMonth.setOvertimePreviousMonth(overtimePreviousMonth);
        return MonthIndex.create(new ContractTermsService(), jsonMonth);
    }

    private void assertOvertime(LocalDate date, LocalTime begin, LocalTime end, Duration expectedOvertime)
    {
        final DayRecord day = createDay(date, begin, end, null, null);
        assertThat(day.getOvertime()).as("overtime").isEqualTo(expectedOvertime);
    }

    private void assertMandatoryBreak(LocalDate date, LocalTime begin, LocalTime end, Duration expectedDuration)
    {
        assertThat(getMandatoryBreak(date, begin, end)).as("mandatory break").isEqualTo(expectedDuration);
    }

    private void assertMandatoryWorkingTime(LocalDate date, LocalTime begin, LocalTime end,
            Duration expectedMandatoryWorkingTime)
    {
        assertThat(getMandatoryWorkingTime(date, begin, end)).as("mandatory working time")
                .isEqualTo(expectedMandatoryWorkingTime);
    }

    private Duration getMandatoryWorkingTime(LocalDate date, LocalTime begin, LocalTime end)
    {
        final DayRecord day = createDay(date, begin, end, null, null);
        return day.getMandatoryWorkingTime();
    }

    private Duration getMandatoryBreak(LocalDate date, LocalTime begin, LocalTime end)
    {
        final DayRecord day = createDay(date, begin, end, null, null);
        return day.getMandatoryBreak();
    }

    private void assertWorkingDay(LocalDate date, boolean expected)
    {
        assertThat(createDay(date).getType().isWorkDay()).isEqualTo(expected);
    }

    private void assertType(LocalDate date, DayType expected)
    {
        final DayRecord day = createDay(date);
        assertDayType(day, expected);
    }

    private void assertDayType(DayRecord day, DayType expected)
    {
        assertThat(day.getType()).isEqualTo(expected);
    }

    private DayRecord createDay(LocalDate date)
    {
        return createDay(date, null, null);
    }

    private DayRecord createDummyDay(LocalDate date)
    {
        return createDay(date);
    }

    private DayRecord createDay(LocalDate date, LocalTime begin, LocalTime end)
    {
        return createDay(date, begin, end, null, null);
    }

    private DayRecord createDay(LocalDate date, LocalTime begin, LocalTime end, DayType type, Duration interruption)
    {
        return createDay(date, begin, end, type, interruption, null);
    }

    private DayRecord createDay(LocalDate date, LocalTime begin, LocalTime end, DayType type, Duration interruption,
            DayRecord previousDay)
    {
        return createDay(date, begin, end, type, interruption, previousDay, null);
    }

    private DayRecord createDay(LocalDate date, LocalTime begin, LocalTime end, DayType type, Duration interruption,
            DayRecord previousDay, MonthIndex month)
    {
        final JsonDay day = new JsonDay();
        day.setBegin(begin);
        day.setEnd(end);
        day.setDate(date);
        day.setType(type);
        day.setInterruption(interruption);
        day.setComment(null);
        return new DayRecord(new ContractTermsService(), day, previousDay, month);
    }
}
