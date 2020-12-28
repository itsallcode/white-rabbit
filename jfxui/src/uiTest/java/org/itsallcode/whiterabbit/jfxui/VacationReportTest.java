package org.itsallcode.whiterabbit.jfxui;

import java.time.Instant;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.TableRowExpectedContent;
import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.VacationReportWindow;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class VacationReportTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void emptyVacationReport()
    {
        final VacationReportWindow report = app().openVacationReport();

        report.getYearsTable().assertRowCount(0);
        report.getMonthTable().assertRowCount(0);

        report.close();
    }

    @Test
    void filledVacationReport()
    {
        time().tickMinute();
        final int row = time().getCurrentDayRowIndex();
        app().dayTable().selectDayTypeDirect(row, DayType.VACATION);

        TestUtil.sleepLong();
        final VacationReportWindow report = app().openVacationReport();

        TestUtil.sleepLong();
        report.getYearsTable().assertRowCount(1)
                .assertContent(TableRowExpectedContent.forValues(Year.of(2007), 0, 3, 1, 2));
        report.getMonthTable().assertRowCount(1)
                .assertContent(TableRowExpectedContent.forValues(YearMonth.of(2007, Month.DECEMBER), 1, "3"));

        report.close();
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
