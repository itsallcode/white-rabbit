package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.*;
import java.util.Locale;

import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.itsallcode.whiterabbit.jfxui.testutil.model.JavaFxTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.*;

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
        dayTable.row(currentDayRowIndex).assertBeginAndEnd(null, null);
    }

    @Test
    void beginAndEndUpdatedEqualAfterFirstMinuteTick()
    {
        time().tickMinute();
        TestUtil.sleepShort();

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final LocalTime now = time().getCurrentTimeMinutes();
        final DayTable dayTable = app().dayTable();
        dayTable.row(currentDayRowIndex).assertBeginAndEnd(now, now);
    }

    @Test
    void beginAndEndUpdatedEqualAfterSecondMinuteTick()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        final LocalTime firstTick = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(firstTick, firstTick);

        time().tickMinute();

        TestUtil.sleepShort();

        final LocalTime secondTick = time().getCurrentTimeMinutes();
        dayTable.row(currentDayRowIndex).assertBeginAndEnd(firstTick, secondTick);
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

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(firstTick, secondTick);
    }

    @Test
    void beginAndEndDeletedWhenChangingDayTypeToSick()
    {
        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        time().tickMinute();
        final LocalTime now = time().getCurrentTimeMinutes();
        TestUtil.sleepShort();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(now, now);

        dayTable.row(currentDayRowIndex).selectDayType(DayType.SICK);
        TestUtil.sleepShort();

        dayTable.row(currentDayRowIndex).assertBeginAndEnd(null, null);
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

    @Test
    void timeWithoutColonSupported()
    {
        assertTimeParsed("0812", LocalTime.of(8, 12));
    }

    @Test
    void timeWithOnlyHourSupported()
    {
        assertTimeParsed("08", LocalTime.of(8, 0));
    }

    private void assertTimeParsed(final String enteredText, final LocalTime expectedTime)
    {
        final int row = time().getCurrentDayRowIndex() + 1;
        final DayTable dayTable = app().dayTable();

        dayTable.row(row).typeBegin(enteredText);

        assertThat(dayTable.row(row).getBegin()).isEqualTo(expectedTime);
    }

    private void assertDurationParsed(final String enteredText, final Duration expectedDuration)
    {
        final int row = time().getCurrentDayRowIndex() + 1;
        final DayTable dayTable = app().dayTable();

        dayTable.row(row).typeInterruption( enteredText);

        assertThat(dayTable.row(row).getInterruption()).isEqualTo(expectedDuration);
    }

    @Test
    void beginFormatted()
    {
        time().tickMinute();
        TestUtil.sleepShort();

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        assertThat(dayTable.row(currentDayRowIndex).getBeginText()).isEqualTo("11:16");
    }

    @Test
    void zeroInterruptionFormatted()
    {
        time().tickMinute();
        TestUtil.sleepShort();

        final int currentDayRowIndex = time().getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();
        assertThat(dayTable.row(currentDayRowIndex).getInterruptionText()).isEqualTo("00:00");
    }

    @Test
    void nonZeroInterruptionFormatted()
    {
        final int row = time().getCurrentDayRowIndex() + 1;
        final DayTable dayTable = app().dayTable();

        dayTable.row(row).typeInterruption( "1:02");

        assertThat(dayTable.row(row).getInterruptionText()).isEqualTo("01:02");
    }

    @Test
    void currentDaySelectedAtStartup()
    {
        time().tickMinute();

        TestUtil.sleepShort();

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
    void start(final Stage stage)
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
