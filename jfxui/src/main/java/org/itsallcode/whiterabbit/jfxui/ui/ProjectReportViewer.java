package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.itsallcode.whiterabbit.jfxui.table.converter.DayTypeStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.DurationStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.ProjectStringConverter;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ReportWindow;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;
import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.itsallcode.whiterabbit.logic.service.project.Project;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LocalDateStringConverter;

public class ProjectReportViewer
{
    private final ProjectReport report;
    private final FormatterService formatterService;
    private final ReportWindow reportWindow;

    public ProjectReportViewer(Stage primaryStage, FormatterService formatterService, ProjectReport report)
    {
        this.reportWindow = new ReportWindow(primaryStage);
        this.formatterService = formatterService;
        this.report = report;
    }

    public void show()
    {
        reportWindow.show(createTreeTable());
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
                        ReportRow::getWorkingTime, new DurationStringConverter(formatterService)),
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
