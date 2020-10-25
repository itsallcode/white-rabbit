package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.itsallcode.whiterabbit.jfxui.testutil.model.JavaFxTable;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class DayTableTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void dayTableRowCount()
    {
        final JavaFxTable<DayRecordPropertyAdapter> dayTable = app().genericDayTable();
        dayTable.assertRowCount(31);
    }

    @Test
    void beginAndEndEmptyByDefault()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();

        final DayTable dayTable = app().dayTable();
        dayTable.assertBeginAndEnd(currentDayRowIndex, null, null);
    }

    @Test
    void beginAndEndUpdatedEqualAfterFirstMinuteTick()
    {
        time().tickMinute();
        TestUtil.sleepShort();

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final LocalTime now = time().getCurrentTimeMinutes();
        final DayTable dayTable = app().dayTable();
        dayTable.assertBeginAndEnd(currentDayRowIndex, now, now);
    }

    @Test
    void beginAndEndUpdatedEqualAfterSecondMinuteTick()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        final LocalTime firstTick = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();

        dayTable.assertBeginAndEnd(currentDayRowIndex, firstTick, firstTick);

        time().tickMinute();

        TestUtil.sleepShort();

        final LocalTime secondTick = time().getCurrentTimeMinutes();
        dayTable.assertBeginAndEnd(currentDayRowIndex, firstTick, secondTick);
    }

    @Test
    void beginAndEndUpdatedEveryMinute()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        final LocalTime firstTick = time().getCurrentTimeMinutes();
        time().tickMinute();
        time().tickMinute();
        time().tickMinute();
        time().tickMinute();
        time().tickMinute();
        final LocalTime secondTick = time().getCurrentTimeMinutes();
        assertThat(secondTick).isEqualTo(firstTick.plusMinutes(5));

        TestUtil.sleepShort();

        dayTable.assertBeginAndEnd(currentDayRowIndex, firstTick, secondTick);
    }

    @Test
    void beginAndEndDeletedWhenChangingDayTypeToSick()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        final LocalTime now = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();

        dayTable.assertBeginAndEnd(currentDayRowIndex, now, now);

        dayTable.selectDayType(currentDayRowIndex, DayType.SICK);

        dayTable.assertBeginAndEnd(currentDayRowIndex, null, null);
    }

    @Test
    void timeWithLeadingZeroSupported()
    {
        assertTimeParsed("08:12", LocalTime.of(8, 12));
    }

    @Test
    void timeAfter12Supported()
    {
        assertTimeParsed("13:14", LocalTime.of(13, 14));
    }

    @Test
    void timeWithoutLeadingZeroSupported()
    {
        assertTimeParsed("8:14", LocalTime.of(8, 14));
    }

    @Test
    void timeWithoutTrailingZeroSupported()
    {
        assertTimeParsed("8:4", LocalTime.of(8, 4));
    }

    @Test
    void durationWithLeadingZeroSupported()
    {
        assertDurationParsed("01:02", Duration.ofHours(1).plusMinutes(2));
    }

    @Test
    void durationWithoutLeadingZeroSupported()
    {
        assertDurationParsed("1:02", Duration.ofHours(1).plusMinutes(2));
    }

    @Test
    void durationWithoutTrailingZeroSupported()
    {
        assertDurationParsed("01:2", Duration.ofHours(1).plusMinutes(2));
    }

    private void assertTimeParsed(String enteredText, LocalTime expectedTime)
    {
        final int row = time().getCurrentDayRowIndex() + 1;
        final DayTable dayTable = app().dayTable();

        dayTable.typeBegin(row, enteredText);

        assertThat(dayTable.getBegin(row)).isEqualTo(expectedTime);
    }

    private void assertDurationParsed(String enteredText, Duration expectedDuration)
    {
        final int row = time().getCurrentDayRowIndex() + 1;
        final DayTable dayTable = app().dayTable();

        dayTable.typeInterruption(row, enteredText);

        assertThat(dayTable.getInterruption(row)).isEqualTo(expectedDuration);
    }

    @Test
    void beginFormatted()
    {
        time().tickMinute();
        TestUtil.sleepShort();

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        assertThat(dayTable.getBeginText(currentDayRowIndex)).isEqualTo("11:16");
    }

    @Test
    void zeroInterruptionFormatted()
    {
        time().tickMinute();
        TestUtil.sleepShort();

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        assertThat(dayTable.getInterruptionText(currentDayRowIndex)).isEqualTo("00:00");
    }

    @Test
    void nonZeroInterruptionFormatted()
    {
        final int row = time().getCurrentDayRowIndex() + 1;
        final DayTable dayTable = app().dayTable();

        dayTable.typeInterruption(row, "1:02");

        assertThat(dayTable.getInterruptionText(row)).isEqualTo("01:02");
    }

    @Test
    void currentDaySelectedAtStartup()
    {
        time().tickMinute();

        assertThat(app().dayTable().getSelectedRow().getDate()).isEqualTo(time().getCurrentDate());
    }

    @Test
    void currentDaySelectedAtDayChange()
    {
        time().tickMinute();

        time().tickDay(LocalTime.of(8, 0));

        assertThat(app().dayTable().getSelectedRow().getDate()).isEqualTo(time().getCurrentDate());
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
