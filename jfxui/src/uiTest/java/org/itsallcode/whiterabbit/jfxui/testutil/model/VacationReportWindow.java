package org.itsallcode.whiterabbit.jfxui.testutil.model;

import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationMonth;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationYear;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.input.KeyCode;
import javafx.stage.Window;

public class VacationReportWindow
{
    private final FxRobot robot;
    private final Window window;
    private final JavaFxTable<VacationYear> yearsTable;
    private final JavaFxTable<VacationMonth> monthTable;

    public VacationReportWindow(FxRobot robot, Window window)
    {
        this.robot = robot;
        this.window = window;

        yearsTable = JavaFxTable.find(robot, "#year-table", VacationYear.class);
        monthTable = JavaFxTable.find(robot, "#month-table", VacationMonth.class);
    }

    public JavaFxTable<VacationYear> getYearsTable()
    {
        return yearsTable;
    }

    public JavaFxTable<VacationMonth> getMonthTable()
    {
        return monthTable;
    }

    public void closeViaButton()
    {
        robot.clickOn("#close-button");
        Assertions.assertThat(window).isNotShowing();
    }

    public void closeViaEscKey()
    {
        robot.type(KeyCode.ESCAPE);
        Assertions.assertThat(window).isNotShowing();
    }
}
