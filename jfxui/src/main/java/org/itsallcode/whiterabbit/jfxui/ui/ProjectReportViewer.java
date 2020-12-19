package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import org.itsallcode.whiterabbit.jfxui.table.converter.DayTypeStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.DurationStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.ProjectStringConverter;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;
import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.itsallcode.whiterabbit.logic.service.project.Project;

import com.sun.javafx.binding.ObjectConstant;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LocalDateStringConverter;

public class ProjectReportViewer
{
    private final ProjectReport report;
    private final Stage primaryStage;
    private final FormatterService formatterService;
    private Stage stage;

    public ProjectReportViewer(Stage primaryStage, FormatterService formatterService, ProjectReport report)
    {
        this.primaryStage = primaryStage;
        this.formatterService = formatterService;
        this.report = report;
    }

    public void show()
    {
        stage = createStage();
        stage.show();
    }

    private Stage createStage()
    {
        final TreeTableView<ReportRow> treeTable = createTreeTable();
        final BorderPane pane = new BorderPane();
        pane.setCenter(treeTable);
        BorderPane.setMargin(treeTable, new Insets(UiResources.GAP_PIXEL));
        return createStage(pane);
    }

    private TreeTableView<ReportRow> createTreeTable()
    {
        final TreeItem<ReportRow> root = new TreeItem<>();

        root.getChildren().addAll(report.days.stream()
                .map(this::createDayTreeItem)
                .collect(toList()));

        final TreeTableView<ReportRow> treeTable = new TreeTableView<>(root);
        treeTable.getColumns().addAll(List.of(column("date", "Date",
                ReportRow::getDate, new LocalDateStringConverter()),
                column("daytype", "Day Type", ReportRow::getDayType,
                        new DayTypeStringConverter()),
                column("project", "Project", ReportRow::getProject,
                        new ProjectStringConverter(null)),
                column("workingtime", "Working time",
                        ReportRow::getWorkingTime, new DurationStringConverter(formatterService)),
                column("comment", "Comment",
                        ReportRow::getComment, new DefaultStringConverter())));

        treeTable.setShowRoot(false);
        treeTable.setEditable(false);
        treeTable.setId("project-table-tree");
        return treeTable;
    }

    private Stage createStage(final Parent root)
    {
        final Stage newStage = new Stage();
        newStage.setTitle("Project report for " + report.month);
        newStage.setScene(new Scene(root, 500, 800));
        newStage.initModality(Modality.NONE);
        newStage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ESCAPE)
            {
                closeReportWindow();
            }
        });
        newStage.initOwner(primaryStage);
        newStage.getIcons().add(UiResources.APP_ICON);
        return newStage;
    }

    private void closeReportWindow()
    {
        this.stage.close();
    }

    private <T> TreeTableColumn<ReportRow, T> column(String id, String label,
            Function<ReportRow, T> valueExtractor,
            StringConverter<T> stringConverter)
    {
        return column(id, label, cellValueFactory(valueExtractor), cellFactory(stringConverter));
    }

    private <T> TreeTableColumn<ReportRow, T> column(String id, String label,
            Callback<CellDataFeatures<ReportRow, T>, ObservableValue<T>> cellValueFactory,
            Callback<TreeTableColumn<ReportRow, T>, TreeTableCell<ReportRow, T>> cellFactory)
    {
        final TreeTableColumn<ReportRow, T> column = new TreeTableColumn<>(label);
        column.setId(id);
        column.setCellValueFactory(cellValueFactory);
        column.setCellFactory(cellFactory);
        column.setEditable(false);
        column.setResizable(true);
        column.setSortable(false);
        return column;
    }

    private <T> Callback<TreeTableColumn<ReportRow, T>, TreeTableCell<ReportRow, T>> cellFactory(
            StringConverter<T> stringConverter)
    {
        return param -> new TextFieldTreeTableCell<>(stringConverter);
    }

    private <T> Callback<CellDataFeatures<ReportRow, T>, ObservableValue<T>> cellValueFactory(
            Function<ReportRow, T> valueExtractor)
    {
        return param -> {
            if (param.getValue() == null || param.getValue().getValue() == null)
            {
                return ObjectConstant.valueOf(null);
            }
            return ObjectConstant.valueOf(valueExtractor.apply(param.getValue().getValue()));
        };
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

    private static class ReportRow
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
