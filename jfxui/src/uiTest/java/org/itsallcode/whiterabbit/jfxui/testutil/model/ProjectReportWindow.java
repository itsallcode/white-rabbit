package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.itsallcode.whiterabbit.api.model.IProject;
import org.itsallcode.whiterabbit.jfxui.ui.ProjectReportViewer.ReportRow;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.input.KeyCode;
import javafx.stage.Window;

public class ProjectReportWindow
{
    private final FxRobot robot;
    private final Window window;
    private final JavaFxTreeTable<ReportRow> table;

    ProjectReportWindow(FxRobot robot, Window window)
    {
        this.robot = robot;
        this.window = window;

        table = JavaFxTreeTable.find(robot, "#project-table-tree", ReportRow.class);
    }

    public void closeViaCloseButton()
    {
        robot.clickOn("#close-button");
        Assertions.assertThat(window).isNotShowing();
    }

    public void closeViaEscKey()
    {
        robot.type(KeyCode.ESCAPE);
        Assertions.assertThat(window).isNotShowing();
    }

    public ProjectReportWindow assertDayCount(int expectedDayCount)
    {
        assertThat(table.getRootChildNodes()).hasSize(expectedDayCount);
        return this;
    }

    public ProjectReportWindow assertProjectCount(int dayIndex, int expectedProjectCount)
    {
        assertThat(table.getChildNodes(dayIndex)).hasSize(expectedProjectCount);
        return this;
    }

    public ProjectReportWindow assertProject(int dayIndex, int projectIndex, IProject expectedProject,
            Duration expectedWorkingTime, String expectedComment)
    {
        final ReportRow row = table.getChildNodes(dayIndex).get(projectIndex);
        assertThat(row.getProject()).isEqualTo(expectedProject);
        assertThat(row.getWorkingTime()).isEqualTo(expectedWorkingTime);
        assertThat(row.getComment()).isEqualTo(expectedComment);
        return this;
    }
}
