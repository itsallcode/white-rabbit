package org.itsallcode.whiterabbit.jfxui;

import java.time.*;
import java.util.Locale;

import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.jfxui.testutil.TableRowExpectedContent;
import org.itsallcode.whiterabbit.jfxui.testutil.model.VacationReportWindow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.*;

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

        report.closeViaButton();
    }

    @Test
    void closeReportByTypingEscKey()
    {
        final VacationReportWindow report = app().openVacationReport();
        report.closeViaEscKey();
    }

    @Test
    void filledVacationReport()
    {
        time().tickMinute();
        final int row = time().getCurrentDayRowIndex();
        app().dayTable().row(row).selectDayTypeDirect(DayType.VACATION);

        final VacationReportWindow report = app().openVacationReport();

        report.getYearsTable().assertRowCount(1)
                .assertContent(TableRowExpectedContent.forValues(Year.of(2007), 0, 3, 1, 2));
        report.getMonthTable().assertRowCount(1)
                .assertContent(TableRowExpectedContent.forValues(YearMonth.of(2007, Month.DECEMBER), 1, "3"));

        report.closeViaButton();
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
