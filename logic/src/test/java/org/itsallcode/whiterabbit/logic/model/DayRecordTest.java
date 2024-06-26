package org.itsallcode.whiterabbit.logic.model;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.storage.data.JsonModelFactory;
import org.itsallcode.whiterabbit.logic.test.TestingConfig;
import org.itsallcode.whiterabbit.logic.test.TestingConfig.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DayRecordTest
{
    @Mock
    private ProjectService projectServiceMock;
    private final ModelFactory modelFactory = new JsonModelFactory();
    private Builder configBuilder;

    @BeforeEach
    void createDefaultConfig()
    {
        configBuilder = TestingConfig.builder();
    }

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
    void testWorkingTimeDoesNotThrowExceptionForMissingBeginTime()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), null, LocalTime.of(16, 0), Duration.ZERO);
    }

    @Test
    void testMandatoryBreakConsidersZeroInterruption()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(16, 0), Duration.ZERO,
                Duration.ofMinutes(45));
    }

    @Test
    void testMandatoryBreakConsidersNonZeroInterruption()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(16, 0), Duration.ofHours(1),
                Duration.ofMinutes(45));
    }

    @Test
    void testMandatoryBreakConsidersInterruptionLessThan6hours()
    {
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(16, 0), Duration.ofHours(3),
                Duration.ZERO);
    }

    @Test
    void testMandatoryBreakReducedByInterruption()
    {
        configBuilder.withReduceMandatoryBreakByInterruption(true);
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(18, 0), Duration.ofMinutes(10),
                Duration.ofMinutes(35));
    }

    @Test
    void testMandatoryBreakNotReducedByInterruptionWhenWorkingLessThan6h()
    {
        configBuilder.withReduceMandatoryBreakByInterruption(true);
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(13, 0), Duration.ofMinutes(10),
                Duration.ZERO);
    }

    @Test
    void testMandatoryBreakZeroForLongerInterruption()
    {
        configBuilder.withReduceMandatoryBreakByInterruption(true);
        assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(18, 0), Duration.ofMinutes(50),
                Duration.ZERO);
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
        day.setBegin(null);
        assertThat(day.getBegin()).isNull();
    }

    @Test
    void setBeginUpdatesRecord()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        day.setBegin(LocalTime.of(22, 33));
        assertThat(day.getBegin()).isEqualTo(LocalTime.of(22, 33));
    }

    @Test
    void setEndAcceptsNullValue()
    {
        final DayRecord day = createDay(LocalDate.of(2020, 4, 22));
        day.setEnd(null);
        assertThat(day.getEnd()).isNull();
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
        assertThatThrownBy(() -> day.setInterruption(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void setInterruptionToZeroUsesNullForJsonRecord()
    {
        final DayData day1 = modelFactory.createDayData();
        day1.setInterruption(Duration.ofHours(1));
        final DayRecord day = dayRecord(day1, null, null);

        day.setInterruption(Duration.ZERO);

        assertThat(day.getInterruption()).isEqualTo(Duration.ZERO);
        assertThat(day1.getInterruption()).isNull();
    }

    @Test
    void setCommentToEmptyStringUsesNullForJsonRecord()
    {
        final DayData day1 = modelFactory.createDayData();
        day1.setComment("comment");
        final DayRecord day = dayRecord(day1, null, null);

        day.setComment("");

        assertThat(day.getComment()).isNull();
        assertThat(day1.getComment()).isNull();
    }

    @Test
    void setCommentToValueSetsCommentForJsonRecord()
    {
        final DayData day1 = modelFactory.createDayData();
        final DayRecord day = dayRecord(day1, null, null);

        day.setComment("comment");

        assertThat(day.getComment()).isEqualTo("comment");
        assertThat(day1.getComment()).isEqualTo("comment");
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
    void getDayDataReturnsMonth()
    {
        final DayData dayData = modelFactory.createDayData();
        final DayRecord day = createDay(dayData);
        assertThat(day.getJsonDay()).isSameAs(dayData);
    }

    @Test
    void getCustomWorkingTimeReturnsEmptyOptionalByDefault()
    {
        final DayData DayData = modelFactory.createDayData();
        DayData.setWorkingHours(null);

        final DayRecord day = createDay(DayData);
        assertThat(day.getCustomWorkingTime()).isEmpty();
    }

    @Test
    void getCustomWorkingTimeReturnsRealValue()
    {
        final DayData DayData = modelFactory.createDayData();
        DayData.setWorkingHours(Duration.ofHours(5));

        final DayRecord day = createDay(DayData);
        assertThat(day.getCustomWorkingTime()).isPresent().contains(Duration.ofHours(5));
    }

    @Test
    void emptyDayIsDummy()
    {
        assertDummyDay(createDummyDay());
    }

    @Test
    void dayWithBeginIsNonDummy()
    {
        final DayRecord day = createDummyDay();
        day.setBegin(LocalTime.of(8, 0));
        assertNonDummyDay(day);
    }

    @Test
    void dayWithEndIsNonDummy()
    {
        final DayRecord day = createDummyDay();
        day.setEnd(LocalTime.of(17, 0));
        assertNonDummyDay(day);
    }

    @Test
    void dayWithTypeIsNonDummy()
    {
        final DayRecord day = createDummyDay();
        day.setType(DayType.WORK);
        assertNonDummyDay(day);
    }

    @Test
    void dayWithNonZeroBreakIsNonDummy()
    {
        final DayRecord day = createDummyDay();
        day.setInterruption(Duration.ofMinutes(30));
        assertNonDummyDay(day);
    }

    @Test
    void dayWithZeroBreakIsDummy()
    {
        final DayRecord day = createDummyDay();
        day.setInterruption(Duration.ZERO);
        assertDummyDay(day);
    }

    @Test
    void dayWithEmptyCommentIsDummy()
    {
        final DayRecord day = createDummyDay();
        day.setComment("");
        assertDummyDay(day);
    }

    @Test
    void dayWithNonEmptyCommentIsDummy()
    {
        final DayRecord day = createDummyDay();
        day.setComment("comment");
        assertNonDummyDay(day);
    }

    @Test
    void dayWithEmptyActivitiesListIsDummy()
    {
        final DayRecord day = createDummyDay();
        day.activities().add();
        day.activities().remove(0);
        assertDummyDay(day);
    }

    @Test
    void dayWithActivitiesListIsNonDummy()
    {
        final DayRecord day = createDummyDay();
        day.activities().add();
        assertNonDummyDay(day);
    }

    private void assertDummyDay(final DayRecord day)
    {
        assertThat(day.isDummyDay()).as("dummy").isTrue();
    }

    private void assertNonDummyDay(final DayRecord day)
    {
        assertThat(day.isDummyDay()).as("dummy").isFalse();
    }

    private MonthIndex month(final LocalDate date, final Duration overtimePreviousMonth, final DayData... days)
    {
        final MonthData jsonMonth = modelFactory.createMonthData();
        jsonMonth.setDays(asList(days));
        jsonMonth.setMonth(date.getMonth());
        jsonMonth.setYear(date.getYear());
        jsonMonth.setOvertimePreviousMonth(overtimePreviousMonth);
        return MonthIndex.create(contractTerms(), projectServiceMock, modelFactory, jsonMonth);
    }

    private void assertOvertime(final LocalDate date, final LocalTime begin, final LocalTime end,
            final Duration expectedOvertime)
    {
        final DayRecord day = createDay(date, begin, end, null, null);
        assertThat(day.getOvertime()).as("overtime").isEqualTo(expectedOvertime);
    }

    private void assertMandatoryBreak(final LocalDate date, final LocalTime begin, final LocalTime end,
            final Duration expectedDuration)
    {
        assertMandatoryBreak(date, begin, end, Duration.ZERO, expectedDuration);
    }

    private void assertMandatoryBreak(final LocalDate date, final LocalTime begin, final LocalTime end,
            final Duration interruption,
            final Duration expectedDuration)
    {
        assertThat(getMandatoryBreak(date, begin, end, interruption)).as("mandatory break").isEqualTo(expectedDuration);
    }

    private void assertMandatoryWorkingTime(final LocalDate date, final LocalTime begin, final LocalTime end,
            final Duration expectedMandatoryWorkingTime)
    {
        assertThat(getMandatoryWorkingTime(date, begin, end)).as("mandatory working time")
                .isEqualTo(expectedMandatoryWorkingTime);
    }

    private Duration getMandatoryWorkingTime(final LocalDate date, final LocalTime begin, final LocalTime end)
    {
        final DayRecord day = createDay(date, begin, end, null, null);
        return day.getMandatoryWorkingTime();
    }

    private Duration getMandatoryBreak(final LocalDate date, final LocalTime begin, final LocalTime end,
            final Duration interruption)
    {
        final DayRecord day = createDay(date, begin, end, null, interruption);
        return day.getMandatoryBreak();
    }

    private void assertWorkingDay(final LocalDate date, final boolean expected)
    {
        assertThat(createDay(date).getType().isWorkDay()).isEqualTo(expected);
    }

    private void assertType(final LocalDate date, final DayType expected)
    {
        final DayRecord day = createDay(date);
        assertDayType(day, expected);
    }

    private void assertDayType(final DayRecord day, final DayType expected)
    {
        assertThat(day.getType()).isEqualTo(expected);
    }

    private DayRecord createDay(final LocalDate date)
    {
        return createDay(date, null, null);
    }

    private DayRecord createDummyDay()
    {
        return createDummyDay(LocalDate.of(2021, 7, 20));
    }

    private DayRecord createDummyDay(final LocalDate date)
    {
        return createDay(date);
    }

    private DayRecord createDay(final LocalDate date, final LocalTime begin, final LocalTime end)
    {
        return createDay(date, begin, end, null, null);
    }

    private DayRecord createDay(final LocalDate date, final LocalTime begin, final LocalTime end, final DayType type,
            final Duration interruption)
    {
        return createDay(date, begin, end, type, interruption, null);
    }

    private DayRecord createDay(final LocalDate date, final LocalTime begin, final LocalTime end, final DayType type,
            final Duration interruption,
            final DayRecord previousDay)
    {
        return createDay(date, begin, end, type, interruption, previousDay, null);
    }

    private DayRecord createDay(final LocalDate date, final LocalTime begin, final LocalTime end, final DayType type,
            final Duration interruption,
            final DayRecord previousDay, final MonthIndex month)
    {
        final DayData day = modelFactory.createDayData();
        day.setBegin(begin);
        day.setEnd(end);
        day.setDate(date);
        day.setType(type);
        day.setInterruption(interruption);
        day.setComment(null);
        return dayRecord(day, previousDay, month);
    }

    private DayRecord createDay(final DayData DayData)
    {
        return new DayRecord(null, DayData, null, null, projectServiceMock, modelFactory);
    }

    private DayRecord dayRecord(final DayData day, final DayRecord previousDay, final MonthIndex month)
    {
        final ContractTermsService contractTerms = contractTerms();
        return new DayRecord(contractTerms, day, previousDay, month, projectServiceMock, modelFactory);
    }

    private ContractTermsService contractTerms()
    {
        return new ContractTermsService(configBuilder.build());
    }
}
