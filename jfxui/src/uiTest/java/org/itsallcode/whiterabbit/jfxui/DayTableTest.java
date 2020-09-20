package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.itsallcode.whiterabbit.jfxui.testutil.model.JavaFxTable;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
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
        final JavaFxTable dayTable = app().genericDayTable();
        Assertions.assertThat(dayTable.table()).hasExactlyNumRows(31);
    }

    @Test
    void beginAndEndEmptyByDefault()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();

        final DayTable dayTable = app().dayTable();
        dayTable.assertBeginAndEnd(currentDayRowIndex, null, null);
    }

    @Test
    void beginAndEndUpdatedEqualAfterFirstMinuteTick()
    {
        tickMinute();
        TestUtil.sleepShort();

        final int currentDayRowIndex = getCurrentDayRowIndex();
        final LocalTime now = getCurrentTimeMinutes();
        final DayTable dayTable = app().dayTable();
        dayTable.assertBeginAndEnd(currentDayRowIndex, now, now);
    }

    @Test
    void beginAndEndUpdatedEqualAfterSecondMinuteTick()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        tickMinute();
        final LocalTime firstTick = getCurrentTimeMinutes();
        TestUtil.sleepShort();

        dayTable.assertBeginAndEnd(currentDayRowIndex, firstTick, firstTick);

        tickMinute();

        TestUtil.sleepShort();

        final LocalTime secondTick = getCurrentTimeMinutes();
        dayTable.assertBeginAndEnd(currentDayRowIndex, firstTick, secondTick);
    }

    @Test
    void beginAndEndUpdatedEveryMinute()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        tickMinute();
        final LocalTime firstTick = getCurrentTimeMinutes();
        tickMinute();
        tickMinute();
        tickMinute();
        tickMinute();
        tickMinute();
        final LocalTime secondTick = getCurrentTimeMinutes();
        assertThat(secondTick).isEqualTo(firstTick.plusMinutes(5));

        TestUtil.sleepShort();

        dayTable.assertBeginAndEnd(currentDayRowIndex, firstTick, secondTick);
    }

    @Test
    void beginAndEndDeletedWhenChangingDayTypeToSick()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();
        final DayTable dayTable = app().dayTable();

        tickMinute();
        final LocalTime now = getCurrentTimeMinutes();
        TestUtil.sleepShort();

        dayTable.assertBeginAndEnd(currentDayRowIndex, now, now);

        dayTable.selectDayType(currentDayRowIndex, DayType.SICK);

        dayTable.assertBeginAndEnd(currentDayRowIndex, null, null);
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setCurrentTime(Instant.parse("2007-12-03T10:15:30.20Z"));
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
