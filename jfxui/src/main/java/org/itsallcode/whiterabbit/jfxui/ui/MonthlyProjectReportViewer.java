package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.joining;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.jfxui.table.converter.DurationStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.ProjectStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.YearMonthStringConverter;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ReportWindow;
import org.itsallcode.whiterabbit.jfxui.uistate.UiStateService;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

public class MonthlyProjectReportViewer
{

    private final ReportWindow reportWindow;
    private final UiStateService uiState;
    private final AppService appService;
    private YearMonth yearMonth;

    public MonthlyProjectReportViewer(final Stage primaryStage, final UiStateService uiState,
            final AppService appService, final YearMonth yearMonth)
    {
        this.uiState = uiState;
        this.appService = appService;
        this.yearMonth = yearMonth;
        this.reportWindow = new ReportWindow(primaryStage, uiState, "monthly-project-report", getWindowTitle());
    }

    public void show()
    {
        final TableView<ReportRow> treeTable = createTreeTable();
        updateTable(treeTable);
        final Node previousMonthButton = UiWidget.button("prev-month-button", "< Previous Month",
                e -> gotoPreviousMonth(treeTable));
        final Node nextMonthButton = UiWidget.button("next-month-button", "Next Month >",
                e -> gotoNextMonth(treeTable));
        reportWindow.show(treeTable, previousMonthButton, nextMonthButton);
        uiState.register(treeTable);
    }

    private void gotoPreviousMonth(final TableView<ReportRow> treeTable)
    {
        yearMonth = yearMonth.minusMonths(1);
        updateTable(treeTable);
        reportWindow.updateTitle(getWindowTitle());
    }

    private void gotoNextMonth(final TableView<ReportRow> treeTable)
    {
        yearMonth = yearMonth.plusMonths(1);
        updateTable(treeTable);
        reportWindow.updateTitle(getWindowTitle());
    }

    private void updateTable(final TableView<ReportRow> treeTable)
    {
        treeTable.setItems(createRows());
    }

    private String getWindowTitle()
    {
        return "Monthly Project Report " + yearMonth;
    }

    private TableView<ReportRow> createTreeTable()
    {
        final TableView<ReportRow> treeTable = new TableView<>();
        treeTable.getColumns().addAll(List.of(
                UiWidget.readOnlyColumn("yearMonth", "Month",
                        new YearMonthStringConverter(), ReportRow::getMonth),
                UiWidget.readOnlyColumn("project", "Project",
                        new ProjectStringConverter(null), ReportRow::getProject),
                UiWidget.readOnlyColumn("workingtime", "Working time",
                        new DurationStringConverter(appService.formatter()), ReportRow::getWorkingTime),
                UiWidget.readOnlyColumn("comment", "Comment",
                        new DefaultStringConverter(), ReportRow::getComment)));

        treeTable.setEditable(false);
        treeTable.setId("monthly-project-table");
        return treeTable;
    }

    private final ObservableList<ReportRow> createRows()
    {
        final ProjectReport report = appService.generateProjectReport(yearMonth);
        return FXCollections.observableList(
                report.getProjects().stream().map(project -> createRow(report.getMonth(), project)).toList());
    }

    private ReportRow createRow(final YearMonth month, final ProjectReportActivity project)
    {
        return new ReportRow(month, project);
    }

    public static class ReportRow
    {
        private final YearMonth month;
        private final ProjectReportActivity project;

        private ReportRow(final YearMonth month, final ProjectReportActivity project)
        {
            this.month = month;
            this.project = project;
        }

        public YearMonth getMonth()
        {
            return month;
        }

        public ProjectImpl getProject()
        {
            return (ProjectImpl) project.getProject();
        }

        public Duration getWorkingTime()
        {
            return project.getWorkingTime();
        }

        public String getComment()
        {
            return project.getComments().stream().collect(joining(", "));
        }
    }
}
