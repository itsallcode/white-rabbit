package org.itsallcode.whiterabbit.jfxui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.property.ClockPropertyFactory;
import org.itsallcode.whiterabbit.jfxui.property.ScheduledProperty;
import org.itsallcode.whiterabbit.jfxui.ui.DayRecordTable;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.AppServiceCallback;
import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.itsallcode.whiterabbit.logic.service.Interruption;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class JavaFxApp extends Application
{
    private static final Logger LOG = LogManager.getLogger(App.class);

    private AppService appService;
    private DayRecordTable dayRecordTable;

    private final ObjectProperty<Interruption> interruption = new SimpleObjectProperty<>();
    private ScheduledProperty<Instant> currentTimeProperty;
    private Stage primaryStage;

    private FormatterService formatter;

    @Override
    public void init() throws Exception
    {
        this.formatter = new FormatterService();
        final Path configFile = Paths.get("time.properties").toAbsolutePath();
        LOG.info("Loading config from {}", configFile);
        final Config config = Config.read(configFile);
        this.appService = AppService.create(config, formatter);
        currentTimeProperty = new ClockPropertyFactory(appService).currentTimeProperty();
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        LOG.info("Starting UI");

        createUi();
        primaryStage.show();

        configureAppService();

        fillRecords(appService.getClock().getCurrentYearMonth());
    }

    @Override
    public void stop()
    {
        currentTimeProperty.cancel();
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
                return showAutomaticInterruptionDialog(startOfInterruption, interruption);
            }

            @Override
            public void recordUpdated(DayRecord record)
            {
                dayRecordTable.recordUpdated(record);
            }
        });
        appService.start();
    }

    private boolean showAutomaticInterruptionDialog(LocalTime startOfInterruption,
            Duration interruption)
    {
        return JavaFxUtil.runOnFxApplicationThread(() -> {
            LOG.info("Showing automatic interruption alert...");
            final Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Add automatic interruption?");
            alert.setHeaderText("An interruption of " + interruption + " was detected beginning at "
                    + startOfInterruption + ".");
            final ButtonType addInterruption = new ButtonType("Add interruption", ButtonData.YES);
            final ButtonType skipInterruption = new ButtonType("Skip interruption", ButtonData.NO);
            alert.getButtonTypes().setAll(addInterruption, skipInterruption);
            final Optional<ButtonType> selectedButton = alert.showAndWait();

            return selectedButton.map(ButtonType::getButtonData) //
                    .filter(d -> d == ButtonData.YES) //
                    .isPresent();
        });
    }

    private void createUi()
    {
        dayRecordTable = new DayRecordTable(record -> appService.store(record), formatter);
        final BorderPane pane = createMainPane();
        final Scene scene = new Scene(pane, 750, 700);
        primaryStage.setScene(scene);
    }

    private BorderPane createMainPane()
    {
        final BorderPane pane = new BorderPane();
        final Node table = dayRecordTable.initTable();
        pane.setCenter(table);
        final int gap = 10;
        final Insets insets = new Insets(gap);
        BorderPane.setMargin(table, insets);
        final Node currentTimeLabel = currentTimeLabel();
        pane.setTop(currentTimeLabel);
        BorderPane.setMargin(currentTimeLabel, insets);

        final Button updateButton = updateButton();
        updateButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        final Button startInterruptionButton = startInterruptionButton();
        startInterruptionButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        final TilePane bottom = new TilePane(Orientation.HORIZONTAL);
        bottom.setHgap(gap);
        bottom.getChildren().addAll(updateButton, startInterruptionButton);

        pane.setBottom(bottom);
        BorderPane.setMargin(bottom, insets);
        return pane;
    }

    private Node currentTimeLabel()
    {
        final Label label = new Label();
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            final Instant now = currentTimeProperty.property().getValue();
            return formatter.formatDateAndtime(now);
        }, currentTimeProperty.property()));
        return label;
    }

    private Button startInterruptionButton()
    {
        final Button button = new Button("Start interruption");
        button.setOnAction(e -> startManualInterruption());
        button.disableProperty().bind(interruption.isNotNull());
        return button;
    }

    private void startManualInterruption()
    {
        if (interruption.isNotNull().get())
        {
            LOG.warn("Interruption {} already active", interruption.get());
            return;
        }
        interruption.set(appService.startInterruption());
        new InterruptionDialog(primaryStage, currentTimeProperty.property(), interruption).show();
    }

    private Button updateButton()
    {
        final Button button = new Button("Update");
        button.setOnAction(e -> appService.updateNow());
        return button;
    }

    private void fillRecords(YearMonth yearMonth)
    {
        appService.getRecords(yearMonth).stream().forEach(dayRecordTable::recordUpdated);
    }
}
