package org.itsallcode.time.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DayRecordTest {

	@BeforeEach
	void setUp() {
	}

	@Test
	void testOnWorkingDayNoMandatoryBreakFor6h() {
		assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(14, 0), Duration.ZERO);
	}

	@Test
	void testOnNonWorkingDayNoMandatoryBreakFor8h() {
		assertMandatoryBreak(LocalDate.of(2018, 10, 6), LocalTime.of(8, 0), LocalTime.of(16, 0), Duration.ZERO);
	}

	@Test
	void testOnWorkingDay30minMandatoryBreakLongerThan6hours() {
		assertMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(14, 1), Duration.ofMinutes(30));
	}

	@Test
	void testWorkingTimeOnWorkingDayNullBeginAndEnd() {
		assertMandatoryBreak(LocalDate.of(2018, 10, 1), null, null, Duration.ZERO);
	}

	@Test
	void testWorkingTimeIsZeroOnWeekend() {
		assertMandatoryBreak(LocalDate.of(2018, 10, 6), LocalTime.of(8, 0), LocalTime.of(14, 1), Duration.ZERO);
	}

	@Test
	void testWorkingTimeThrowsExceptionForMissingEndTime() {
		assertThrows(IllegalStateException.class, () -> getMandatoryBreak(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), null));
	}

	@Test
	void testWorkingTimeThrowsExceptionForMissingBeginTime() {
		assertThrows(IllegalStateException.class, () -> getMandatoryBreak(LocalDate.of(2018, 10, 1), null, LocalTime.of(8, 0)));
	}

	@Test
	void testWorkingTime1h() {
		final DayRecord day = createDay(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(9, 0), null, null);
		assertThat(day.getWorkingTime()).isEqualTo(Duration.ofMinutes(60));
		assertThat(day.getMandatoryBreak()).isEqualTo(Duration.ZERO);
		assertThat(day.getOvertime()).isEqualTo(Duration.ofHours(-7));
	}

	@Test
	void testMondayIsNoWorkingDay() {
		assertWorkingDay(LocalDate.of(2018, 10, 1), true);
	}

	@Test
	void testSaturdayIsNoWorkingDay() {
		assertWorkingDay(LocalDate.of(2018, 10, 6), false);
	}

	@Test
	void testSundayIsNoWorkingDay() {
		assertWorkingDay(LocalDate.of(2018, 10, 7), false);
	}

	@Test
	void testMondayIsWorkdayType() {
		assertType(LocalDate.of(2018, 10, 1), DayType.WORKING);
	}

	@Test
	void testSaturdayIsWeekendType() {
		assertType(LocalDate.of(2018, 10, 6), DayType.WEEKEND);
	}

	@Test
	void testSundayIsWeekendType() {
		assertType(LocalDate.of(2018, 10, 7), DayType.WEEKEND);
	}

	@Test
	void testDayWithTypeSetReturnsType() {
		assertDayType(createDay(null, null, null, DayType.HOLIDAY, null), DayType.HOLIDAY);
	}

	@Test
	void testNegativeOvertimeOnWorkingDay() {
		assertOvertime(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(9, 0), Duration.ofHours(-7));
	}

	@Test
	void testZeroOvertimeOnWorkingDay() {
		assertOvertime(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(16, 30), Duration.ZERO);
	}

	@Test
	void testPositiveOvertimeOnWorkingDay() {
		assertOvertime(LocalDate.of(2018, 10, 1), LocalTime.of(8, 0), LocalTime.of(17, 0), Duration.ofMinutes(30));
	}

	@Test
	void testPositiveOvertimeOnWeekendDay() {
		assertOvertime(LocalDate.of(2018, 10, 6), LocalTime.of(8, 0), LocalTime.of(9, 0), Duration.ofHours(1));
	}

	@Test
	void testPositiveOvertimeOnWeekendDayWithoutBreak() {
		assertOvertime(LocalDate.of(2018, 10, 6), LocalTime.of(8, 0), LocalTime.of(16, 30), Duration.ofHours(8).plusMinutes(30));
	}

	@Test
	void testDefaultInterruptionIsZero() {
		final DayRecord day = createDay(null, null, null, null, null);
		assertThat(day.getInterruption()).isEqualTo(Duration.ZERO);
	}

	@Test
	void testInterruptionIsReturned() {
		final DayRecord day = createDay(null, null, null, null, Duration.ofMinutes(30));
		assertThat(day.getInterruption()).isEqualTo(Duration.ofMinutes(30));
	}

	private void assertOvertime(LocalDate date, LocalTime begin, LocalTime end, Duration expectedOvertime) {
		final DayRecord day = createDay(date, begin, end, null, null);
		final Duration overtime = day.getOvertime();
		assertThat(overtime).isEqualTo(expectedOvertime);
	}

	private void assertMandatoryBreak(LocalDate date, LocalTime begin, LocalTime end, Duration expectedDuration) {
		assertThat(getMandatoryBreak(date, begin, end)).isEqualTo(expectedDuration);
	}

	private Duration getMandatoryBreak(LocalDate date, LocalTime begin, LocalTime end) {
		final DayRecord day = createDay(date, begin, end, null, null);
		return day.getMandatoryBreak();
	}

	private void assertWorkingDay(LocalDate date, boolean expected) {
		assertThat(createDay(date).isWorkingDay()).isEqualTo(expected);
	}

	private void assertType(LocalDate date, DayType expected) {
		final DayRecord day = createDay(date);
		assertDayType(day, expected);
	}

	private void assertDayType(DayRecord day, DayType expected) {
		assertThat(day.getType()).isEqualTo(expected);
	}

	private DayRecord createDay(LocalDate date) {
		return createDay(date, null, null, null, null);
	}

	private DayRecord createDay(LocalDate date, LocalTime begin, LocalTime end, DayType type, Duration interruption) {
		final JsonDay day = new JsonDay();
		day.setBegin(begin);
		day.setEnd(end);
		day.setDate(date);
		day.setType(type);
		day.setInterruption(interruption);
		return new DayRecord(day);
	}
}
