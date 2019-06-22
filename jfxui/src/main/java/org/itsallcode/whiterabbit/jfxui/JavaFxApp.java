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
import org.itsallcode.whiterabbit.jfxui.table.DayRecordTable;
import org.itsallcode.whiterabbit.jfxui.tray.Tray;
import org.itsallcode.whiterabbit.jfxui.tray.TrayCallback;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.AppServiceCallback;
import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.itsallcode.whiterabbit.logic.service.Interruption;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class JavaFxApp extends Application
{
    private static final Logger LOG = LogManager.getLogger(App.class);
    private static final int GAP_PIXEL = 10;

    private AppService appService;
    private DayRecordTable dayRecordTable;

    private final ObjectProperty<Interruption> interruption = new SimpleObjectProperty<>();
    private final ObjectProperty<MonthIndex> currentMonth = new SimpleObjectProperty<>();
    private ScheduledProperty<Instant> currentTimeProperty;
    private Stage primaryStage;

    private FormatterService formatter;

    private Tray tray;

    @Override
    public void init() throws Exception
    {
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, exception) -> showErrorDialog(exception));
        try
        {
            doInitialize();
        }
        catch (final Exception e)
        {
            LOG.error("Exception during initialization: " + e.getMessage(), e);
            showErrorDialog(e);
            Platform.exit();
        }
    }

    private void doInitialize()
    {
        this.formatter = new FormatterService();
        final Path configFile = Paths.get("time.properties").toAbsolutePath();
        LOG.info("Loading config from {}", configFile);
        final Config config = Config.read(configFile);
        this.appService = AppService.create(config, formatter);
        currentTimeProperty = new ClockPropertyFactory(appService).currentTimeProperty();
        tray = Tray.create(new TrayCallback()
        {
            @Override
            public void showMainWindow()
            {
                Platform.runLater(() -> {
                    if (primaryStage.isShowing())
                    {
                        LOG.debug("Request focus");
                        primaryStage.requestFocus();
                    }
                    else
                    {
                        LOG.debug("Show primary stage");
                        primaryStage.show();
                    }
                });
            }

            @Override
            public void startInterruption()
            {
                startManualInterruption();
            }

            @Override
            public void exit()
            {
                Platform.exit();
            }
        });
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        LOG.info("Starting UI");
        try
        {
            doStart(primaryStage);
        }
        catch (final Exception e)
        {
            LOG.error("Exception during start: " + e.getMessage(), e);
            showErrorDialog(e);
            Platform.exit();
        }
    }

    private void doStart(Stage primaryStage)
    {
        createUi();
        primaryStage.show();

        configureAppService();

        loadInitialData();
    }

    private void loadInitialData()
    {
        final YearMonth thisMonth = appService.getClock().getCurrentYearMonth();
        currentMonth.setValue(appService.getMonth(thisMonth));
    }

    @Override
    public void stop()
    {
        LOG.info("Stopping application");
        tray.removeTrayIcon();
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
                JavaFxUtil.runOnFxApplicationThread(() -> currentMonth.setValue(record.getMonth()));
            }

            @Override
            public void exceptionOccured(Exception e)
            {
                showErrorDialog(e);
            }
        });
        appService.start();
    }

    private void showErrorDialog(Throwable e)
    {
        final String message = "An error occured: " + e.getClass() + ": " + e.getMessage();
        LOG.error(message, e);
        JavaFxUtil.runOnFxApplicationThread(() -> {
            final Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
            alert.show();
        });
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
        dayRecordTable = new DayRecordTable(currentMonth, record -> {
            appService.store(record);
            appService.updateNow();
        }, formatter);
        final BorderPane pane = createMainPane();
        final Scene scene = new Scene(pane, 800, 800);
        scene.getStylesheets().add("org/itsallcode/whiterabbit/jfxui/table/style.css");
        primaryStage.setTitle("White Rabbit Time Recording");
        primaryStage.getIcons().add(new Image(JavaFxApp.class.getResourceAsStream("/icon.png")));

        if (tray.isSupported())
        {
            LOG.debug("System tray is supported: allow hiding primary stage");
            Platform.setImplicitExit(false);
            primaryStage.setOnCloseRequest(event -> {
                LOG.info("Hiding primary stage");
                event.consume();
                primaryStage.hide();
            });
        }
        else
        {
            LOG.debug("System tray is not supported: don't allow hiding primary stage");
        }

        primaryStage.setScene(scene);
    }

    private BorderPane createMainPane()
    {
        final BorderPane pane = new BorderPane();
        final Node table = dayRecordTable.initTable();
        pane.setCenter(table);

        final Insets insets = new Insets(GAP_PIXEL);
        BorderPane.setMargin(table, insets);

        final Node currentTimeLabel = currentTimeLabel();
        pane.setTop(currentTimeLabel);
        BorderPane.setMargin(currentTimeLabel, insets);

        final Node buttonBar = createButtonBar();

        pane.setBottom(buttonBar);
        BorderPane.setMargin(buttonBar, insets);
        return pane;
    }

    private TilePane createButtonBar()
    {
        final Button startInterruptionButton = button("Start interruption",
                e -> startManualInterruption());
        startInterruptionButton.disableProperty().bind(interruption.isNotNull());
        final TilePane bottom = new TilePane(Orientation.HORIZONTAL);
        bottom.setHgap(GAP_PIXEL);

        bottom.getChildren().addAll(button("Update", e -> appService.updateNow()), //
                startInterruptionButton, //
                button("Update overtime for all months",
                        e -> appService.updatePreviousMonthOvertimeField()), //
                button("Quit", e -> Platform.exit()));
        return bottom;
    }

    private Node currentTimeLabel()
    {
        final Label label = new Label();
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            final Instant now = currentTimeProperty.property().getValue();
            String text = formatter.formatDateAndtime(now);
            final MonthIndex month = currentMonth.get();
            if (month != null && month.getOvertimePreviousMonth() != null)
            {
                final Duration totalOvertime = month.getTotalOvertime();
                text += ", current month: " + month.getYearMonth() //
                        + ", overtime previous month: "
                        + formatter.format(month.getOvertimePreviousMonth())
                        + ", overtime this month: "
                        + formatter.format(totalOvertime.minus(month.getOvertimePreviousMonth()))
                        + ", total overtime: " + formatter.format(totalOvertime);
            }
            return text;
        }, currentTimeProperty.property(), currentMonth));
        return label;
    }

    private Button button(String label, EventHandler<ActionEvent> action)
    {
        final Button button = new Button(label);
        button.setOnAction(action);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return button;
    }

    private void startManualInterruption()
    {
        if (interruption.isNotNull().get())
        {
            LOG.warn("Interruption {} already active", interruption.get());
            return;
        }
        JavaFxUtil.runOnFxApplicationThread(() -> {
            interruption.set(appService.startInterruption());
            new InterruptionDialog(primaryStage, currentTimeProperty.property(), interruption)
                    .show();
        });
    }
}
