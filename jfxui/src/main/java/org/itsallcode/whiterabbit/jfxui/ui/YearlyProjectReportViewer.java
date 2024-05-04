package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.joining;

import java.time.Duration;
import java.util.List;

import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.jfxui.table.converter.DurationStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.ProjectStringConverter;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ReportWindow;
import org.itsallcode.whiterabbit.jfxui.uistate.UiStateService;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class YearlyProjectReportViewer
{
    private final ProjectReport report;
    private final ReportWindow reportWindow;
    private final UiStateService uiState;
    private final AppService appService;

    public YearlyProjectReportViewer(final Stage primaryStage, final UiStateService uiState,
            final AppService appService,
            final ProjectReport report)
    {
        this.uiState = uiState;
        this.appService = appService;
        this.reportWindow = new ReportWindow(primaryStage, uiState, "yearly-project-report", "Yearly Project Report");
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
                        .map(project -> createRow(report.getMonth().getYear(), project)).toList());
        final TableView<ReportRow> treeTable = new TableView<>(rows);
        treeTable.getColumns().addAll(List.of(
                UiWidget.readOnlyColumn("year", "Year",
                        new IntegerStringConverter(), ReportRow::getYear),
                UiWidget.readOnlyColumn("project", "Project",
                        new ProjectStringConverter(null), ReportRow::getProject),
                UiWidget.readOnlyColumn("workingtime", "Working time",
                        new DurationStringConverter(appService.formatter()), ReportRow::getWorkingTime),
                UiWidget.readOnlyColumn("comment", "Comment",
                        new DefaultStringConverter(), ReportRow::getComment)));

        treeTable.setEditable(false);
        treeTable.setId("yearly-project-table");
        return treeTable;
    }

    private static ReportRow createRow(final int year, final ProjectReportActivity project)
    {
        return new ReportRow(year, project);
    }

    public static final class ReportRow
    {
        private final int year;
        private final ProjectReportActivity project;

        private ReportRow(final int year, final ProjectReportActivity project)
        {
            this.year = year;
            this.project = project;
        }

        public int getYear()
        {
            return year;
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
