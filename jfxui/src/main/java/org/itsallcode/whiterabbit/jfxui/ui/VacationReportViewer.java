package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.joining;

import java.time.Year;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.table.converter.YearMonthStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.YearStringConverter;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationMonth;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationYear;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class VacationReportViewer
{
    private static final Logger LOG = LogManager.getLogger(VacationReportViewer.class);

    private final SimpleObjectProperty<Year> selectedYear = new SimpleObjectProperty<>(null);
    private final VacationReport report;
    private final Stage primaryStage;

    private Stage stage;

    public VacationReportViewer(Stage primaryStage, VacationReport report)
    {
        this.primaryStage = primaryStage;
        this.report = report;
    }

    public void show()
    {
        stage = createStage();
        stage.show();
    }

    private Stage createStage()
    {
        final TableView<VacationYear> yearsTable = createYearsTable();
        final TableView<VacationMonth> monthsTable = createMonthsTable();

        final SplitPane mainPane = new SplitPane(yearsTable, monthsTable);
        mainPane.setOrientation(Orientation.VERTICAL);
        mainPane.setDividerPositions(0.2);

        final BorderPane pane = new BorderPane();
        pane.setTop(createToolBar());
        pane.setCenter(mainPane);
        BorderPane.setMargin(mainPane, UiResources.DEFAULT_MARGIN);
        return createStage(pane);
    }

    private Stage createStage(final Parent root)
    {
        final Stage newStage = new Stage();
        newStage.setTitle("Project report");
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
        stage.close();
    }

    private ToolBar createToolBar()
    {
        return new ToolBar(UiWidget.button("close-button", "Close Report", e -> closeReportWindow()));
    }

    private TableView<VacationMonth> createMonthsTable()
    {
        final TableView<VacationMonth> table = new TableView<>(FXCollections.observableArrayList(report.months));
        table.setEditable(false);
        table.getColumns().addAll(createMonthTableColumns());
        table.setId("month-table");
        return table;
    }

    private List<TableColumn<VacationMonth, ?>> createMonthTableColumns()
    {
        return List.of(
                UiWidget.readOnlyColumn("month", "Month", new YearMonthStringConverter(), VacationMonth::getYearMonth),
                UiWidget.readOnlyColumn("day-count-used", "Day count used", new IntegerStringConverter(),
                        VacationMonth::getUsedVacationDayCount),
                UiWidget.readOnlyColumn("days-used", "Days used", new DefaultStringConverter(),
                        month -> month.getVacationDaysUsed().stream()
                                .map(date -> date.getDayOfMonth())
                                .map(String::valueOf)
                                .collect(joining(", "))));
    }

    private TableView<VacationYear> createYearsTable()
    {
        final TableView<VacationYear> table = new TableView<>(FXCollections.observableArrayList(report.years));
        table.setEditable(false);
        table.getColumns().addAll(createYearTableColumns());
        table.setId("year-table");
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    LOG.debug("Table row selected: {}", newValue.getYear());
                    selectedYear.set(newValue.getYear());
                });
        return table;
    }

    private List<TableColumn<VacationYear, ?>> createYearTableColumns()
    {
        return List.of(
                UiWidget.readOnlyColumn("year", "Year", new YearStringConverter(), VacationYear::getYear),
                UiWidget.readOnlyColumn("days-previous-year", "Rem. days prev. year",
                        new IntegerStringConverter(), VacationYear::getDaysRemaingFromPreviousYear),
                UiWidget.readOnlyColumn("days-available", "Days available", new IntegerStringConverter(),
                        VacationYear::getDaysAvailable),
                UiWidget.readOnlyColumn("days-used", "Days used", new IntegerStringConverter(),
                        VacationYear::getDaysUsed),
                UiWidget.readOnlyColumn("days-remaining", "Days remaining", new IntegerStringConverter(),
                        VacationYear::getDaysRemaining));
    }
}
