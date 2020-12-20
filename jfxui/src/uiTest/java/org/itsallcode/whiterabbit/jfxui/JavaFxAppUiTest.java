package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.scene.control.Labeled;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class JavaFxAppUiTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void currentTimeAndOvertimeLabelsUpdated()
    {
        final Labeled timeLabel = robot.lookup("#current-time-label").queryLabeled();
        final Labeled overtimeLabel = robot.lookup("#overtime-label").queryLabeled();

        assertAll(
                () -> Assertions.assertThat(timeLabel).hasText("03.12.07, 11:15:30"),
                () -> Assertions.assertThat(overtimeLabel)
                        .hasText("Overtime previous month: 00:00, this month: 00:00, total: 00:00"));

        time().tickSecond();
        assertAll(
                () -> Assertions.assertThat(timeLabel).hasText("03.12.07, 11:15:31"),
                () -> Assertions.assertThat(overtimeLabel)
                        .hasText("Overtime previous month: 00:00, this month: 00:00, total: 00:00"));

        time().tickMinute();
        assertAll(
                () -> Assertions.assertThat(timeLabel).hasText("03.12.07, 11:16:31"),
                () -> Assertions.assertThat(overtimeLabel)
                        .hasText("Overtime previous month: 00:00, this month: -08:00, total: -08:00"));

        time().tickMinute();
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime previous month: 00:00, this month: -07:59, total: -07:59");
    }

    @Test
    void dayTableUpdatedEveryMinute()
    {
        final Labeled timeLabel = robot.lookup("#current-time-label").queryLabeled();
        final Labeled overtimeLabel = robot.lookup("#overtime-label").queryLabeled();

        assertAll(
                () -> Assertions.assertThat(timeLabel).hasText("03.12.07, 11:15:30"),
                () -> Assertions.assertThat(overtimeLabel)
                        .hasText("Overtime previous month: 00:00, this month: 00:00, total: 00:00"));

        time().tickSecond();
        assertAll(
                () -> Assertions.assertThat(timeLabel).hasText("03.12.07, 11:15:31"),
                () -> Assertions.assertThat(overtimeLabel)
                        .hasText("Overtime previous month: 00:00, this month: 00:00, total: 00:00"));

        time().tickMinute();
        assertAll(
                () -> Assertions.assertThat(timeLabel).hasText("03.12.07, 11:16:31"),
                () -> Assertions.assertThat(overtimeLabel)
                        .hasText("Overtime previous month: 00:00, this month: -08:00, total: -08:00"));

        time().tickMinute();
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime previous month: 00:00, this month: -07:59, total: -07:59");
    }

    @Test
    void jsonFileWrittenAfterMinuteTick()
    {
        final LocalDate today = time().getCurrentDate();

        time().tickMinute();
        final LocalTime begin = time().getCurrentTimeMinutes();

        time().tickMinute();
        final LocalTime end = time().getCurrentTimeMinutes();

        final JsonMonth month = loadMonth(today);

        assertAll(
                () -> assertThat(month.getDays()).hasSize(1),
                () -> assertThat(month.getDays()).extracting(JsonDay::getBegin).containsExactly(begin),
                () -> assertThat(month.getDays()).extracting(JsonDay::getEnd).containsExactly(end));
    }

    @Test
    void newMonthSelectedWhenMonthChanges()
    {
        assertAll(
                () -> assertThat(app().getSelectedMonth()).isEqualTo(YearMonth.of(2007, Month.DECEMBER)),
                () -> app().dayTable().assertDate(0, LocalDate.of(2007, Month.DECEMBER, 1)));

        time().tickDay(LocalDateTime.of(2008, Month.JANUARY, 2, 8, 15, 0));
        TestUtil.sleepShort();

        assertAll(
                () -> assertThat(app().getSelectedMonth()).isEqualTo(YearMonth.of(2008, Month.JANUARY)),
                () -> app().dayTable().assertDate(0, LocalDate.of(2008, Month.JANUARY, 1)));
    }

    @Test
    void newMonthSelectedUserChangesMonth()
    {
        time().tickDay(LocalDateTime.of(2008, Month.JANUARY, 2, 8, 15, 0));

        assertAll(
                () -> assertThat(app().getSelectedMonth()).isEqualTo(YearMonth.of(2008, Month.JANUARY)),
                () -> app().dayTable().assertDate(0, LocalDate.of(2008, Month.JANUARY, 1)));

        app().setSelectedMonth(YearMonth.of(2007, Month.DECEMBER));
        TestUtil.sleepShort();

        assertAll(
                () -> assertThat(app().getSelectedMonth()).isEqualTo(YearMonth.of(2007, Month.DECEMBER)),
                () -> app().dayTable().assertDate(0, LocalDate.of(2007, Month.DECEMBER, 1)));
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setInitialTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        doStart(stage);
        setRobot(robot);
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
