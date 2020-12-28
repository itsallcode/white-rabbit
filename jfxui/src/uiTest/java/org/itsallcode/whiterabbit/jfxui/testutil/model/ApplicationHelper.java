package org.itsallcode.whiterabbit.jfxui.testutil.model;

import java.time.Duration;
import java.time.YearMonth;
import java.util.Objects;

import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.testutil.UiDebugTool;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.service.query.NodeQuery;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

public class ApplicationHelper
{
    private final FxRobot robot;
    private final Window window;

    private ApplicationHelper(FxRobot robot, Window window)
    {
        this.robot = robot;
        this.window = window;
    }

    public static ApplicationHelper create(FxRobot robot)
    {
        Objects.requireNonNull(robot, "robot");
        return new ApplicationHelper(robot, robot.window(0));
    }

    public InterruptionDialog startInterruption()
    {
        final Button startInterruptionButton = robot.lookup("#start-interruption-button").queryButton();
        robot.clickOn(startInterruptionButton);
        final Window window = robot.window("Add interruption");
        return new InterruptionDialog(robot, window);
    }

    public JavaFxTable<DayRecordPropertyAdapter> genericDayTable()
    {
        return JavaFxTable.find(robot, "#day-table", DayRecordPropertyAdapter.class);
    }

    public DayTable dayTable()
    {
        return new DayTable(genericDayTable(), robot);
    }

    public ActivitiesTable activitiesTable()
    {
        return new ActivitiesTable(genericActivitiesTable(), robot);
    }

    private JavaFxTable<ActivityPropertyAdapter> genericActivitiesTable()
    {
        return JavaFxTable.find(robot, "#activities-table", ActivityPropertyAdapter.class);
    }

    public AutomaticInterruptionDialog assertAutomaticInterruption()
    {
        final Window dialogWindow = robot.window("Interruption detected");
        Assertions.assertThat(dialogWindow).isShowing();
        return new AutomaticInterruptionDialog(robot, dialogWindow);
    }

    public AboutDialogWindow openAboutDialog()
    {
        robot.clickOn("#menu_help").clickOn("#menuitem_about");
        final Window dialogWindow = robot.window("About White Rabbit");
        Assertions.assertThat(dialogWindow).isShowing();
        return new AboutDialogWindow(robot, dialogWindow);
    }

    public void addPresetInterruption(Duration preset)
    {
        final SplitMenuButton splitMenuButton = robot.lookup("#add-interruption-button").query();
        final StackPane arrowButton = (StackPane) robot.from(splitMenuButton).lookup(".arrow-button").query();
        robot.clickOn(arrowButton);

        robot.clickOn("#add-interruption-preset-" + preset.toString());
    }

    public AddInterruptionDialog addInterruption()
    {
        robot.clickOn("#add-interruption-button");
        final Window dialogWindow = robot.window("Add interruption for today");
        Assertions.assertThat(dialogWindow).isShowing();
        return new AddInterruptionDialog(robot, dialogWindow);
    }

    public YearMonth getSelectedMonth()
    {
        return getSelectedMonthComboBox().getValue();
    }

    public void setSelectedMonth(YearMonth month)
    {
        JavaFxUtil.runOnFxApplicationThread(() -> getSelectedMonthComboBox().setValue(month));
    }

    private ComboBox<YearMonth> getSelectedMonthComboBox()
    {
        return robot.lookup("#selected-month-combobox").queryComboBox();
    }

    public ProjectReportWindow openProjectReport()
    {
        robot.clickOn("#menu_reports");
        robot.clickOn("#menuitem_project_report");

        Window.getWindows().forEach(w -> UiDebugTool.printNode(w.getScene().getRoot()));
        final Window window = robot.window("Project report");
        Assertions.assertThat(window).isShowing();
        return new ProjectReportWindow(robot, window);
    }

    private NodeQuery lookup(String query)
    {
        return robot.from(window.getScene().getRoot()).lookup(query);
    }

    public VacationReportWindow openVacationReport()
    {
        robot.clickOn("#menu_reports");
        robot.clickOn("#menuitem_vacation_report");

        final Window window = robot.window("Vacation report");
        Assertions.assertThat(window).isShowing();
        return new VacationReportWindow(robot, window);
    }
}
