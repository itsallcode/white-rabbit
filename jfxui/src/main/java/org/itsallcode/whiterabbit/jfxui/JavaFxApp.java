package org.itsallcode.whiterabbit.jfxui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.YearMonth;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class JavaFxApp extends Application
{
    private static final Logger LOG = LogManager.getLogger(App.class);

    private AppService appService;

    private final ObservableList<DayRecord> dayRecords = FXCollections.observableArrayList();

    @Override
    public void init() throws Exception
    {
        final FormatterService formatterService = new FormatterService(Locale.US);
        final Path configFile = Paths.get("time.properties").toAbsolutePath();
        LOG.info("Loading config from {}", configFile);
        final Config config = Config.read(configFile);
        this.appService = AppService.create(config, formatterService);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        LOG.info("Starting UI");
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        final ColumnConstraints colConstraint = new ColumnConstraints();
        colConstraint.setPercentWidth(100);
        grid.getColumnConstraints().add(colConstraint);
        final RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.setPercentHeight(100);
        grid.getRowConstraints().add(rowConstraint);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setGridLinesVisible(true);

        final Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> appService.updateNow());
        appService.setUpdateListener(this::recordUpdated);
        appService.startAutoUpdate();

        fillRecords();
        final TableView<DayRecord> table = new TableView<>(dayRecords);
        table.setEditable(true);

        table.getColumns().add(tableColumn("Date", "date"));
        table.getColumns().add(tableColumn("Type", "type"));
        table.getColumns().add(tableColumn("Begin", "begin"));
        table.getColumns().add(tableColumn("End", "end"));
        table.getColumns().add(tableColumn("Break", "mandatoryBreak"));
        table.getColumns().add(tableColumn("Interruption", "interruption"));
        table.getColumns().add(tableColumn("Comment", "comment"));

        grid.add(updateButton, 0, 1);
        GridPane.setFillHeight(table, true);
        GridPane.setFillWidth(table, true);
        grid.add(table, 0, 0);

        final Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fillRecords()
    {
        appService.getRecords(YearMonth.of(2019, Month.MAY)).stream().forEach(this::recordUpdated);
    }

    private <T> TableColumn<DayRecord, T> tableColumn(String label, String property)
    {
        final TableColumn<DayRecord, T> dateColumn = new TableColumn<>(label);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>(property));
        return dateColumn;
    }

    private void recordUpdated(DayRecord record)
    {
        final int recordIndex = record.getDate().getDayOfMonth() - 1;
        while (dayRecords.size() <= recordIndex)
        {
            dayRecords.add(null);
        }
        dayRecords.set(recordIndex, record);
    }
}
