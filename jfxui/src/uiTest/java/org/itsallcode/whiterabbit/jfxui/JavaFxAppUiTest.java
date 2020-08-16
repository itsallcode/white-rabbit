package org.itsallcode.whiterabbit.jfxui;

import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.scene.control.Labeled;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class JavaFxAppUiTest extends JavaFxAppUiTestBase
{

    @Test
    void currentTimeAndOvertimeLabelsUpdated(FxRobot robot) throws InterruptedException
    {
        System.out.println("test currentTimeAndOvertimeLabelsUpdated");

        final Labeled timeLabel = robot.lookup("#current-time-label").queryLabeled();
        final Labeled overtimeLabel = robot.lookup("#overtime-label").queryLabeled();

        Assertions.assertThat(timeLabel).hasText("Current time: 03.12.07, 11:15:30");
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: 00:00, total: 00:00");

        tickSecond();
        Assertions.assertThat(timeLabel).hasText("Current time: 03.12.07, 11:15:31");
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: 00:00, total: 00:00");

        tickMinute();
        Assertions.assertThat(timeLabel).hasText("Current time: 03.12.07, 11:16:31");
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: -08:00, total: -08:00");

        tickMinute();
        Assertions.assertThat(overtimeLabel)
                .hasText("Overtime: previous month: 00:00, this month: -07:59, total: -07:59");
    }

    @Test
    void dayTableRowCount(FxRobot robot) throws InterruptedException
    {
        System.out.println("test dayTableRowCount");
        final TableView<DayRecordPropertyAdapter> dayTable = robot.lookup("#day-table").queryTableView();
        Assertions.assertThat(dayTable).hasExactlyNumRows(31);
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
