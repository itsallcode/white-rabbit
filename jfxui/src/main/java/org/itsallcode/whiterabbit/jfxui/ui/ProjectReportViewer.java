package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.jfxui.UiActions;
import org.itsallcode.whiterabbit.jfxui.table.converter.DayTypeStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.DurationStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.ProjectStringConverter;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ProgressDialog;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ProgressDialog.DialogProgressMonitor;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ReportWindow;
import org.itsallcode.whiterabbit.jfxui.uistate.UiStateService;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.plugin.AppPlugin;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
        final Node[] exportButtons = getExportButtons();
        reportWindow.show(treeTable, exportButtons);
        uiState.register(treeTable);
    }

    private Node[] getExportButtons()
    {
        return appService.pluginManager().findPluginsSupporting(ProjectReportExporter.class).stream()
                .map(this::createExportButton)
                .toArray(Node[]::new);
    }

    private Button createExportButton(AppPlugin plugin)
    {
        final String pluginId = plugin.getId();
        final EventHandler<ActionEvent> action = e -> exportReport(plugin);
        return UiWidget.button(pluginId + "-export-button", "Export to " + pluginId, action);
    }

    private void exportReport(AppPlugin plugin)
    {
        final var projectReportExporter = plugin.getFeature(ProjectReportExporter.class)
                .orElseThrow(() -> new IllegalStateException(
                        "Plugin " + plugin + " does not support " + ProjectReportExporter.class));
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

        root.getChildren().addAll(report.getDays().stream()
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

    private TreeItem<ReportRow> createDayTreeItem(ProjectReportDay day)
    {
        final TreeItem<ReportRow> treeItem = new TreeItem<>(new ReportRow(day));
        treeItem.setExpanded(true);
        treeItem.getChildren().addAll(
                day.getProjects().stream()
                        .map(project -> new ReportRow(day, project))
                        .map(TreeItem::new)
                        .collect(toList()));
        return treeItem;
    }

    public static class ReportRow
    {
        private final LocalDate date;
        private final DayType dayType;
        private final ProjectImpl project;
        private final Duration workingTime;
        private final String comment;

        private ReportRow(ProjectReportDay day)
        {
            this(day, null);
        }

        private ReportRow(ProjectReportDay day, ProjectReportActivity project)
        {
            if (project == null)
            {
                this.date = day.getDate();
                this.dayType = day.getType();
                this.project = null;
                this.workingTime = day.getProjects().stream()
                        .map(ProjectReportActivity::getWorkingTime)
                        .reduce((a, b) -> a.plus(b))
                        .orElse(Duration.ZERO);
                this.comment = day.getComment();
            }
            else
            {
                this.date = null;
                this.dayType = null;
                this.project = (ProjectImpl) project.getProject();
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

        public ProjectImpl getProject()
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
