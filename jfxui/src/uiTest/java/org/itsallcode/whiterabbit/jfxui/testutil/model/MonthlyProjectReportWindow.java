package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.itsallcode.whiterabbit.api.model.Project;
import org.itsallcode.whiterabbit.jfxui.testutil.TestUtil;
import org.itsallcode.whiterabbit.jfxui.ui.MonthlyProjectReportViewer;
import org.itsallcode.whiterabbit.jfxui.ui.MonthlyProjectReportViewer.ReportRow;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MonthlyProjectReportWindow
{
    private final FxRobot robot;
    private final Window window;

    private final JavaFxTable<MonthlyProjectReportViewer.ReportRow> table;

    MonthlyProjectReportWindow(final FxRobot robot, final Window window)
    {
        this.robot = robot;
        this.window = window;

        table = JavaFxTable.find(robot, "#monthly-project-table", ReportRow.class);
    }

    public void closeViaCloseButton()
    {
        robot.clickOn("#close-button");
        Assertions.assertThat(window).isNotShowing();
    }

    public void gotoPreviousMonth()
    {
        robot.clickOn("#prev-month-button");
    }

    public void gotoNextMonth()
    {
        robot.clickOn("#next-month-button");
    }

    public void closeViaEscKey()
    {
        robot.type(KeyCode.ESCAPE);
        Assertions.assertThat(window).isNotShowing();
    }

    public MonthlyProjectReportWindow assertWindowTitle(final String expectedTitle)
    {
        final String title = ((Stage) window).getTitle();
        TestUtil.retryAssertion(Duration.ofSeconds(5), () -> assertThat(title).isEqualTo(expectedTitle));
        return this;
    }

    public MonthlyProjectReportWindow assertRowCount(final int expectedRowCount)
    {
        assertThat(table.getRowCount()).isEqualTo(expectedRowCount);
        return this;
    }

    public MonthlyProjectReportWindow assertProject(final int projectIndex, final Project expectedProject,
            final Duration expectedWorkingTime, final String expectedComment)
    {
        final ReportRow row = table.row(projectIndex).tableRow().getItem();
        assertThat(row.getProject()).isEqualTo(expectedProject);
        assertThat(row.getWorkingTime()).isEqualTo(expectedWorkingTime);
        assertThat(row.getComment()).isEqualTo(expectedComment);
        return this;
    }
}
