package org.itsallcode.whiterabbit.jfxui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.ui.DayRecordTable;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class JavaFxApp extends Application
{
    private static final Logger LOG = LogManager.getLogger(App.class);

    private AppService appService;
    private final DayRecordTable dayRecordTable = new DayRecordTable();

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
    public void start(Stage primaryStage)
    {
        LOG.info("Starting UI");

        configureAppService();

        createUi(primaryStage);
        primaryStage.show();

        fillRecords(appService.getClock().getCurrentYearMonth());
    }

    private void configureAppService()
    {
        appService.setUpdateListener(dayRecordTable::recordUpdated);
        appService.startAutoUpdate();
    }

    private void createUi(Stage primaryStage)
    {
        final GridPane grid = createGridPane();
        grid.add(updateButton(), 0, 1);

        final Node tableNode = dayRecordTable.initTable();
        GridPane.setFillHeight(tableNode, true);
        GridPane.setFillWidth(tableNode, true);
        grid.add(tableNode, 0, 0);

        final Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);
    }

    private Button updateButton()
    {
        final Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> appService.updateNow());
        return updateButton;
    }

    private GridPane createGridPane()
    {
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
        grid.setPadding(new Insets(15, 5, 15, 5));
        grid.setGridLinesVisible(true);
        return grid;
    }

    private void fillRecords(YearMonth yearMonth)
    {
        appService.getRecords(yearMonth).stream().forEach(dayRecordTable::recordUpdated);
    }
}
