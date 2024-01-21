package org.itsallcode.whiterabbit.jfxui.testutil.model;

import java.time.Duration;
import java.time.YearMonth;
import java.util.Objects;

import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

public class ApplicationHelper
{
    private final FxRobot robot;

    private ApplicationHelper(FxRobot robot, Window window)
    {
        this.robot = robot;
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
        final Window window = robot.window("Interruption detected");
        Assertions.assertThat(window).isShowing();
        return new AutomaticInterruptionDialog(robot, window);
    }

    public void assertNoAutomaticInterruption()
    {
        final Window window = robot.window("Interruption detected");
        Assertions.assertThat(window).isNotShowing();
    }

    public AboutDialogWindow openAboutDialog()
    {
        robot.clickOn("#menu_help").clickOn("#menuitem_about");
        final Window window = robot.window("About White Rabbit");
        Assertions.assertThat(window).isShowing();
        return new AboutDialogWindow(robot, window);
    }

    public PluginManagerWindow openPluginManager()
    {
        robot.clickOn("#menu_plugins").clickOn("#menuitem_pluginmanager");
        final Window window = robot.window("Plugin Manager");
        Assertions.assertThat(window).isShowing();
        return new PluginManagerWindow(robot, window);
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
        final Window window = robot.window("Add interruption for today");
        Assertions.assertThat(window).isShowing();
        return new AddInterruptionDialog(robot, window);
    }

    public YearMonth getSelectedMonth()
    {
        return getSelectedMonthComboBox().getValue();
    }

    public void setSelectedMonth(YearMonth month)
    {
        JavaFxUtil.runOnFxApplicationThread(() -> getSelectedMonthComboBox().setValue(month));
    }

    public void gotoNextMonth()
    {
        clickButton("#next-month-button");
    }

    public void gotoPreviousMonth()
    {
        clickButton("#previous-month-button");
    }

    private void clickButton(String query)
    {
        final Button button = robot.lookup(query).queryButton();
        robot.clickOn(button);
    }

    private ComboBox<YearMonth> getSelectedMonthComboBox()
    {
        return robot.lookup("#selected-month-combobox").queryComboBox();
    }

    public DailyProjectReportWindow openDailyProjectReport()
    {
        robot.clickOn("#menu_reports");
        robot.clickOn("#menuitem_daily_project_report");

        final Window window = robot.window("Daily Project Report");
        Assertions.assertThat(window).isShowing();
        return new DailyProjectReportWindow(robot, window);
    }

    public MonthlyProjectReportWindow openMonthlyProjectReport()
    {
        robot.clickOn("#menu_reports");
        robot.clickOn("#menuitem_monthly_project_report");

        final Window window = robot.window("Monthly Project Report");
        Assertions.assertThat(window).isShowing();
        return new MonthlyProjectReportWindow(robot, window);
    }

    public VacationReportWindow openVacationReport()
    {
        robot.clickOn("#menu_reports");
        robot.clickOn("#menuitem_vacation_report");

        final Window window = robot.window("Vacation Report");
        Assertions.assertThat(window).isShowing();
        return new VacationReportWindow(robot, window);
    }
}
