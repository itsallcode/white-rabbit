package org.itsallcode.time.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.itsallcode.time.model.json.JsonDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DayRecordTest {

	@BeforeEach
	void setUp() {
	}

	@Test
	void testNoMandatoryBreakFor6h() {
		final DayRecord day = createDay(LocalTime.of(8, 0), LocalTime.of(14, 0));
		assertThat(day.getMandatoryBreak()).isEqualTo(Duration.ZERO);
	}

	@Test
	void test30minMandatoryBreakLongerThan6hours() {
		final DayRecord day = createDay(LocalTime.of(8, 0), LocalTime.of(14, 1));
		assertThat(day.getMandatoryBreak()).isEqualTo(Duration.ofMinutes(30));
	}

	@Test
	void testWorkingTimeNullBeginAndEnd() {
		final DayRecord day = createDay(null, null);
		assertThat(day.getWorkingTime()).isEqualTo(Duration.ofMinutes(0));
	}

	@Test
	void testWorkingTime1h() {
		final DayRecord day = createDay(LocalTime.of(8, 0), LocalTime.of(9, 0));
		assertThat(day.getWorkingTime()).isEqualTo(Duration.ofMinutes(60));
		assertThat(day.getMandatoryWorkingTime()).isEqualTo(Duration.ofHours(8));
		assertThat(day.getMandatoryBreak()).isEqualTo(Duration.ZERO);
		assertThat(day.getOvertime()).isEqualTo(Duration.ofHours(-7));
	}

	private DayRecord createDay(LocalTime begin, LocalTime end) {
		final JsonDay day = new JsonDay();
		day.setBegin(begin);
		day.setEnd(end);
		day.setDate(LocalDate.of(2018, 10, 1));
		return new DayRecord(day);
	}
}
