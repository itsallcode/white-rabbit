package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Locale;

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
        final JavaFxTable dayTable = JavaFxTable.findDayTable(robot);
        Assertions.assertThat(dayTable.table()).hasExactlyNumRows(31);
    }

    @Test
    void beginAndEndEmptyByDefault()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();

        final DayTable dayTable = DayTable.find(robot);
        dayTable.assertBeginAndEnd(currentDayRowIndex, null, null);
    }

    @Test
    void beginAndEndUpdatedEqualAfterFirstMinuteTick()
    {
        tickMinute();
        sleepShort();

        final int currentDayRowIndex = getCurrentDayRowIndex();
        final LocalTime now = getCurrentTimeMinutes();
        final DayTable dayTable = DayTable.find(robot);
        dayTable.assertBeginAndEnd(currentDayRowIndex, now, now);
    }

    @Test
    void beginAndEndUpdatedEqualAfterSecondMinuteTick()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();
        final DayTable dayTable = DayTable.find(robot);

        tickMinute();
        final LocalTime firstTick = getCurrentTimeMinutes();
        sleepShort();

        dayTable.assertBeginAndEnd(currentDayRowIndex, firstTick, firstTick);

        tickMinute();

        sleepShort();

        final LocalTime secondTick = getCurrentTimeMinutes();
        dayTable.assertBeginAndEnd(currentDayRowIndex, firstTick, secondTick);
    }

    @Test
    void beginAndEndUpdatedEveryMinute()
    {
        final int currentDayRowIndex = getCurrentDayRowIndex();
        final DayTable dayTable = DayTable.find(robot);

        tickMinute();
        final LocalTime firstTick = getCurrentTimeMinutes();
        tickMinute();
        tickMinute();
        tickMinute();
        tickMinute();
        tickMinute();
        final LocalTime secondTick = getCurrentTimeMinutes();
        assertThat(secondTick).isEqualTo(firstTick.plusMinutes(5));

        sleepShort();

        dayTable.assertBeginAndEnd(currentDayRowIndex, firstTick, secondTick);
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setCurrentTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        doStart(stage);
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
