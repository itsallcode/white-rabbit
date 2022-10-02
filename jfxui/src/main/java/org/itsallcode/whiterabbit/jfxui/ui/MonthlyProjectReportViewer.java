package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

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
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

public class MonthlyProjectReportViewer
{
    private final ProjectReport report;
    private final ReportWindow reportWindow;
    private final UiStateService uiState;
    private final AppService appService;

    public MonthlyProjectReportViewer(Stage primaryStage, UiStateService uiState, AppService appService,
            ProjectReport report)
    {
        this.uiState = uiState;
        this.appService = appService;
        this.reportWindow = new ReportWindow(primaryStage, uiState, "monthly-project-report", "Monthly Project Report");
        this.report = report;
    }

    public void show()
    {
        final TableView<ReportRow> treeTable = createTreeTable();
        reportWindow.show(treeTable);
        uiState.register(treeTable);
    }

    private TableView<ReportRow> createTreeTable()
    {
        final ObservableList<ReportRow> rows = FXCollections.observableList(
                report.getProjects().stream()
                        .map(project -> createRow(report.getMonth(), project)).collect(toList()));
        final TableView<ReportRow> treeTable = new TableView<>(rows);
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

    private ReportRow createRow(YearMonth month, ProjectReportActivity project)
    {
        return new ReportRow(month, project);
    }

    public static class ReportRow
    {
        private final YearMonth month;
        private final ProjectReportActivity project;

        private ReportRow(YearMonth month, ProjectReportActivity project)
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
