package org.itsallcode.whiterabbit.jfxui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.ui.DayRecordTable;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.AppServiceCallback;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class JavaFxApp extends Application
{
    private static final Logger LOG = LogManager.getLogger(App.class);

    private AppService appService;
    private DayRecordTable dayRecordTable;

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

        createUi(primaryStage);
        primaryStage.show();

        configureAppService();

        fillRecords(appService.getClock().getCurrentYearMonth());
    }

    @Override
    public void stop()
    {
        appService.shutdown();
    }

    private void configureAppService()
    {
        appService.setUpdateListener(new AppServiceCallback()
        {
            @Override
            public boolean shouldAddAutomaticInterruption(LocalTime startOfInterruption,
                    Duration interruption)
            {
                return JavaFxUtil.runOnFxApplicationThread(() -> {
                    LOG.info("Showing automatic interruption alert...");
                    final Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Add automatic interruption?");
                    alert.setHeaderText("An interruption of " + interruption
                            + " was detected beginning at " + startOfInterruption + ".");
                    final ButtonType addInterruption = new ButtonType("Add interruption",
                            ButtonData.YES);
                    final ButtonType skipInterruption = new ButtonType("Skip interruption",
                            ButtonData.NO);
                    alert.getButtonTypes().setAll(addInterruption, skipInterruption);
                    final Optional<ButtonType> selectedButton = alert.showAndWait();

                    return selectedButton.map(ButtonType::getButtonData) //
                            .filter(d -> d == ButtonData.YES) //
                            .isPresent();
                });
            }

            @Override
            public void recordUpdated(DayRecord record)
            {
                dayRecordTable.recordUpdated(record);
            }
        });
        appService.startAutoUpdate();
    }

    private void createUi(Stage primaryStage)
    {
        final GridPane grid = createGridPane();
        grid.add(updateButton(), 0, 1);

        dayRecordTable = new DayRecordTable(record -> appService.store(record));
        final Node tableNode = dayRecordTable.initTable();
        GridPane.setFillHeight(tableNode, true);
        GridPane.setFillWidth(tableNode, true);
        grid.add(tableNode, 0, 0);

        final Scene scene = new Scene(grid, 650, 600);
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
        grid.setGridLinesVisible(false);
        return grid;
    }

    private void fillRecords(YearMonth yearMonth)
    {
        appService.getRecords(yearMonth).stream().forEach(dayRecordTable::recordUpdated);
    }
}
