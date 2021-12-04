package org.itsallcode.whiterabbit.jfxui.testutil.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.itsallcode.whiterabbit.api.model.Project;
import org.itsallcode.whiterabbit.jfxui.ui.DailyProjectReportViewer.ReportRow;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;

public class DailyProjectReportWindow
{
    private final FxRobot robot;
    private final Window window;
    private final JavaFxTreeTable<ReportRow> table;

    DailyProjectReportWindow(FxRobot robot, Window window)
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

    public DailyProjectReportWindow assertDayCount(int expectedDayCount)
    {
        assertThat(table.getRootChildNodes()).hasSize(expectedDayCount);
        return this;
    }

    public DailyProjectReportWindow assertProjectCount(int dayIndex, int expectedProjectCount)
    {
        assertThat(table.getChildNodes(dayIndex)).hasSize(expectedProjectCount);
        return this;
    }

    public DailyProjectReportWindow assertProject(int dayIndex, int projectIndex, Project expectedProject,
            Duration expectedWorkingTime, String expectedComment)
    {
        final ReportRow row = table.getChildNodes(dayIndex).get(projectIndex);
        assertThat(row.getProject()).isEqualTo(expectedProject);
        assertThat(row.getWorkingTime()).isEqualTo(expectedWorkingTime);
        assertThat(row.getComment()).isEqualTo(expectedComment);
        return this;
    }

    public void assertExportButtons(String... buttonLabels)
    {
        for (final String label : buttonLabels)
        {
            final Button exportButton = robot.from(robot.rootNode(window)).lookup(label).queryButton();
            Assertions.assertThat(exportButton).as("Button " + label).isEnabled();
        }
    }
}
