package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.joining;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.table.converter.YearMonthStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.YearStringConverter;
import org.itsallcode.whiterabbit.jfxui.ui.widget.ReportWindow;
import org.itsallcode.whiterabbit.jfxui.uistate.UiStateService;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationMonth;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationYear;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class VacationReportViewer
{
    private static final Logger LOG = LogManager.getLogger(VacationReportViewer.class);

    private final SimpleObjectProperty<Year> selectedYear = new SimpleObjectProperty<>(null);
    private final VacationReport report;

    private final ReportWindow reportWindow;
    private final UiStateService uiState;

    public VacationReportViewer(Stage primaryStage, UiStateService uiState, VacationReport report)
    {
        this.uiState = uiState;
        this.reportWindow = new ReportWindow(primaryStage, uiState, "vacation-report", "Vacation Report");
        this.report = report;
    }

    public void show()
    {
        final TableView<VacationYear> yearsTable = createYearsTable();
        final TableView<VacationMonth> monthsTable = createMonthsTable();

        final SplitPane mainPane = new SplitPane(yearsTable, monthsTable);
        mainPane.setId("vacationReportMainPane");
        mainPane.setOrientation(Orientation.VERTICAL);
        mainPane.setDividerPositions(0.2);
        final Node reportView = mainPane;

        reportWindow.show(reportView);

        uiState.register(mainPane);
        uiState.register(yearsTable);
        uiState.register(monthsTable);
    }

    private TableView<VacationMonth> createMonthsTable()
    {
        final TableView<VacationMonth> table = new TableView<>(FXCollections.observableArrayList(report.months));
        table.setEditable(false);
        table.getColumns().addAll(createMonthTableColumns());
        table.setId("vacation-report-month-table");
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
                                .map(LocalDate::getDayOfMonth)
                                .map(String::valueOf)
                                .collect(joining(", "))));
    }

    private TableView<VacationYear> createYearsTable()
    {
        final TableView<VacationYear> table = new TableView<>(FXCollections.observableArrayList(report.years));
        table.setEditable(false);
        table.getColumns().addAll(createYearTableColumns());
        table.setId("vacation-report-year-table");
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
