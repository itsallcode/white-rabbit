package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.UiActions;
import org.itsallcode.whiterabbit.jfxui.table.converter.DayTypeStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.DurationStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.ProjectStringConverter;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ProgressDialog;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ProgressDialog.DialogProgressMonitor;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ReportWindow;
import org.itsallcode.whiterabbit.jfxui.uistate.UiStateService;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.plugin.ProjectReportExporter;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LocalDateStringConverter;

public class ProjectReportViewer
{
    private static final Logger LOG = LogManager.getLogger(ProjectReportViewer.class);

    private final ProjectReport report;
    private final ReportWindow reportWindow;
    private final UiStateService uiState;
    private final AppService appService;
    private final UiActions uiActions;

    private final Stage primaryStage;

    public ProjectReportViewer(Stage primaryStage, UiStateService uiState, AppService appService, UiActions uiActions,
            ProjectReport report)
    {
        this.primaryStage = primaryStage;
        this.uiState = uiState;
        this.appService = appService;
        this.uiActions = uiActions;
        this.reportWindow = new ReportWindow(primaryStage, uiState, "project-report", "Project Report");
        this.report = report;
    }

    public void show()
    {
        final TreeTableView<ReportRow> treeTable = createTreeTable();
        reportWindow.show(treeTable,
                UiWidget.button("pmsmart-export-button", "Export to PMSmart", e -> exportReport("pmsmart")));
        uiState.register(treeTable);
    }

    private void exportReport(String pluginId)
    {
        final ProjectReportExporter projectReportExporter = appService.pluginManager()
                .getProjectReportExporter(pluginId);
        final DialogProgressMonitor progressMonitor = ProgressDialog.show(primaryStage, "Exporting project report...");
        appService.scheduler().schedule(Duration.ZERO, () -> {
            projectReportExporter.export(report, progressMonitor);
            progressMonitor.done();
        },
                throwable -> {
                    progressMonitor.done();
                    LOG.error("Error exporting project report", throwable);
                    uiActions.showErrorDialog(throwable.getMessage());
                });
    }

    private TreeTableView<ReportRow> createTreeTable()
    {
        final TreeItem<ReportRow> root = new TreeItem<>();

        root.getChildren().addAll(report.days.stream()
                .map(this::createDayTreeItem)
                .collect(toList()));

        final TreeTableView<ReportRow> treeTable = new TreeTableView<>(root);
        treeTable.getColumns().addAll(List.of(
                UiWidget.treeTableColumn("date", "Date",
                        ReportRow::getDate, new LocalDateStringConverter()),
                UiWidget.treeTableColumn("daytype", "Day Type", ReportRow::getDayType,
                        new DayTypeStringConverter()),
                UiWidget.treeTableColumn("project", "Project", ReportRow::getProject,
                        new ProjectStringConverter(null)),
                UiWidget.treeTableColumn("workingtime", "Working time",
                        ReportRow::getWorkingTime, new DurationStringConverter(appService.formatter())),
                UiWidget.treeTableColumn("comment", "Comment",
                        ReportRow::getComment, new DefaultStringConverter())));

        treeTable.setShowRoot(false);
        treeTable.setEditable(false);
        treeTable.setId("project-table-tree");
        return treeTable;
    }

    private TreeItem<ReportRow> createDayTreeItem(Day day)
    {
        final TreeItem<ReportRow> treeItem = new TreeItem<>(new ReportRow(day));
        treeItem.setExpanded(true);
        treeItem.getChildren().addAll(
                day.projects.stream()
                        .map(project -> new ReportRow(day, project))
                        .map(TreeItem::new)
                        .collect(toList()));
        return treeItem;
    }

    public static class ReportRow
    {
        private final LocalDate date;
        private final DayType dayType;
        private final Project project;
        private final Duration workingTime;
        private final String comment;

        private ReportRow(Day day)
        {
            this(day, null);
        }

        private ReportRow(Day day, ProjectActivity project)
        {
            if (project == null)
            {
                this.date = day.date;
                this.dayType = day.type;
                this.project = null;
                this.workingTime = day.projects.stream()
                        .map(ProjectActivity::getWorkingTime)
                        .reduce((a, b) -> a.plus(b))
                        .orElse(Duration.ZERO);
                this.comment = day.comment;
            }
            else
            {
                this.date = null;
                this.dayType = null;
                this.project = project.getProject();
                this.workingTime = project.getWorkingTime();
                this.comment = project.getComment();
            }
        }

        public LocalDate getDate()
        {
            return date;
        }

        public DayType getDayType()
        {
            return dayType;
        }

        public Project getProject()
        {
            return project;
        }

        public Duration getWorkingTime()
        {
            return workingTime;
        }

        public String getComment()
        {
            return comment;
        }
    }
}
