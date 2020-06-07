package org.itsallcode.whiterabbit.jfxui;

import java.lang.ProcessHandle.Info;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.activities.ActivitiesTable;
import org.itsallcode.whiterabbit.jfxui.property.ClockPropertyFactory;
import org.itsallcode.whiterabbit.jfxui.property.ScheduledProperty;
import org.itsallcode.whiterabbit.jfxui.splashscreen.ProgressPreloaderNotification;
import org.itsallcode.whiterabbit.jfxui.splashscreen.ProgressPreloaderNotification.Type;
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
import org.itsallcode.whiterabbit.logic.service.singleinstance.OtherInstance;
import org.itsallcode.whiterabbit.logic.service.singleinstance.RunningInstanceCallback;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReport;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class JavaFxApp extends Application
{
    private static final Logger LOG = LogManager.getLogger(JavaFxApp.class);
    private static final int GAP_PIXEL = 10;
    private static final String MESSAGE_BRING_TO_FRONT = "bringToFront";
    private static final String MESSAGE_GET_PROCESS_INFO = "getProcessInfo";

    private AppService appService;
    private DayRecordTable dayRecordTable;
    private ActivitiesTable activitiesTable;

    private final ObjectProperty<Interruption> interruption = new SimpleObjectProperty<>();
    private final ObjectProperty<MonthIndex> currentMonth = new SimpleObjectProperty<>();
    private final BooleanProperty stoppedWorkingForToday = new SimpleBooleanProperty(false);
    private final ObservableList<YearMonth> availableMonths = FXCollections.observableArrayList();

    private ScheduledProperty<Instant> currentTimeProperty;
    private Stage primaryStage;

    private FormatterService formatter;

    private Tray tray;
    private Locale locale;

    @Override
    public void init()
    {
        try
        {
            doInitialize();
        }
        catch (final Exception e)
        {
            stop();
            throw e;
        }
    }

    private void notifyPreloaderProgress(Type notificationType)
    {
        notifyPreloader(new ProgressPreloaderNotification(this, notificationType));
    }

    private void doInitialize()
    {
        final Config config = readConfig();
        this.locale = config.getLocale();
        this.formatter = new FormatterService(locale);
        this.appService = AppService.create(config, formatter);
        final Optional<OtherInstance> otherInstance = appService
                .registerSingleInstance(this::messageFromOtherInstanceReceived);
        if (otherInstance.isPresent())
        {
            final String response = otherInstance.get().sendMessageWithResponse(MESSAGE_GET_PROCESS_INFO);
            otherInstance.get().sendMessage(MESSAGE_BRING_TO_FRONT);
            throw new IllegalStateException("Other instance already running: " + response);
        }

        currentTimeProperty = new ClockPropertyFactory(appService).currentTimeProperty();
        tray = Tray.create(new TrayCallback()
        {
            @Override
            public void showMainWindow()
            {
                bringWindowToFront();
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

    private void bringWindowToFront()
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

    private Config readConfig()
    {
        final Path configFile = Paths.get("time.properties").toAbsolutePath();
        LOG.info("Loading config from {}", configFile);
        return Config.read(configFile);
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        LOG.info("Starting UI");
        doStart(primaryStage);
        notifyPreloaderProgress(Type.STARTUP_FINISHED);
    }

    private void doStart(Stage primaryStage)
    {
        createUi();
        primaryStage.show();

        startAppService();

        loadInitialData();
    }

    private void loadInitialData()
    {
        final YearMonth thisMonth = appService.getClock().getCurrentYearMonth();
        loadMonth(thisMonth);
    }

    private void loadMonth(final YearMonth month)
    {
        final MonthIndex record = appService.getOrCreateMonth(month);
        currentMonth.setValue(record);
    }

    @Override
    public void stop()
    {
        LOG.info("Stopping application");
        if (tray != null)
        {
            tray.removeTrayIcon();
        }
        if (currentTimeProperty != null)
        {
            currentTimeProperty.cancel();
        }
        if (appService != null)
        {
            appService.close();
        }
        Platform.exit();
    }

    private void startAppService()
    {
        appService.setUpdateListener(new AppServiceCallbackImplementation());
        appService.start();
    }

    private void messageFromOtherInstanceReceived(String message, RunningInstanceCallback.ClientConnection client)
    {
        if (MESSAGE_BRING_TO_FRONT.equals(message))
        {
            bringWindowToFront();
        }
        if (MESSAGE_GET_PROCESS_INFO.equals(message))
        {
            final Info info = ProcessHandle.current().info();
            final String response = "PID: " + ProcessHandle.current().pid() //
                    + ", user: " + info.user().orElse("n/a") //
                    + ", command: " + info.command().orElse("n/a") //
                    + ", arguments: " + Arrays.toString(info.arguments().orElse(new String[0])) //
                    + ", startup: " + info.startInstant().orElse(null) //
                    + ", uptime: "
                    + info.startInstant().map(start -> Duration.between(start, Instant.now())).orElse(null);
            client.sendMessage(response);
        }
    }

    public void showVacationReport()
    {
        final VacationReport vacationReport = appService.getVacationReport();
        new VacationReportViewer(vacationReport).show();
    }

    private void createUi()
    {
        LOG.debug("Creating user interface");
        dayRecordTable = new DayRecordTable(locale, currentMonth, record -> {
            appService.store(record);
            appService.updateNow();
        }, formatter);

        activitiesTable = new ActivitiesTable(dayRecordTable.selectedDay());
        final MenuBar menuBar = new MenuBarBuilder(this, appService, this.stoppedWorkingForToday).build();
        final BorderPane rootPane = new BorderPane(createMainPane());
        rootPane.setTop(menuBar);
        final Scene scene = new Scene(rootPane, 780, 800);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F5)
            {
                appService.updateNow();
            }
        });

        primaryStage.setTitle("White Rabbit Time Recording");
        primaryStage.getIcons().add(new Image(JavaFxApp.class.getResourceAsStream("/icon.png")));

        if (tray.isSupported())
        {
            LOG.trace("System tray is supported: allow hiding primary stage");
            Platform.setImplicitExit(false);
            primaryStage.setOnCloseRequest(event -> {
                LOG.debug("Hiding primary stage");
                event.consume();
                primaryStage.hide();
            });
        }
        else
        {
            LOG.debug("System tray is not supported: don't allow hiding primary stage");
        }

        primaryStage.setScene(scene);
        LOG.debug("User interface finished");
    }

    private BorderPane createMainPane()
    {
        final BorderPane pane = new BorderPane();
        final Node table = dayRecordTable.initTable();
        pane.setCenter(table);

        final Insets insets = new Insets(GAP_PIXEL);
        BorderPane.setMargin(table, insets);

        final FlowPane topPane = new FlowPane();
        topPane.getChildren().add(new Label("Month:"));
        topPane.getChildren().add(monthDropDownBox());
        topPane.getChildren().add(currentTimeLabel());
        pane.setTop(topPane);
        BorderPane.setMargin(topPane, insets);

        final Node buttonBar = createButtonBar();

        pane.setBottom(buttonBar);
        BorderPane.setMargin(buttonBar, insets);

        activitiesTable.initTable();
        return pane;
    }

    private TilePane createButtonBar()
    {
        final Button startInterruptionButton = button("Start interruption", e -> startManualInterruption());
        startInterruptionButton.disableProperty().bind(interruption.isNotNull());
        final TilePane bottom = new TilePane(Orientation.HORIZONTAL);
        bottom.setHgap(GAP_PIXEL);

        bottom.getChildren().addAll(button("Update", e -> appService.updateNow()), //
                startInterruptionButton, //
                createStopWorkForTodayButton(), //
                button("Vacation report", e -> showVacationReport()));
        return bottom;
    }

    private Button createStopWorkForTodayButton()
    {
        final Button button = new Button();
        button.textProperty()
                .bind(Bindings.createStringBinding(
                        () -> stoppedWorkingForToday.get() ? "Continue working" : "Stop working for today",
                        stoppedWorkingForToday));
        button.setOnAction(e -> appService.toggleStopWorkForToday());
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return button;
    }

    public void exitApp()
    {
        Platform.exit();
    }

    private Node monthDropDownBox()
    {
        availableMonths.addAll(appService.getAvailableDataYearMonth());
        final ComboBox<YearMonth> comboBox = new ComboBox<>(availableMonths);

        currentMonth.addListener(
                (observable, oldValue, newValue) -> comboBox.getSelectionModel().select(newValue.getYearMonth()));
        comboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> loadMonth(newValue));
        return comboBox;
    }

    private Node currentTimeLabel()
    {
        final Label label = new Label();
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            final Instant now = currentTimeProperty.property().getValue();
            String text = formatter.formatDateAndTime(now);
            final MonthIndex month = currentMonth.get();
            if (month != null && month.getOvertimePreviousMonth() != null)
            {
                final Duration totalOvertime = month.getTotalOvertime();
                text += ", overtime previous month: " + formatter.format(month.getOvertimePreviousMonth())
                        + ", overtime this month: "
                        + formatter.format(totalOvertime.minus(month.getOvertimePreviousMonth())) + ", total overtime: "
                        + formatter.format(totalOvertime);
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
            new InterruptionDialog(primaryStage, currentTimeProperty.property(), interruption).show();
        });
    }

    private final class AppServiceCallbackImplementation implements AppServiceCallback
    {
        @Override
        public boolean shouldAddAutomaticInterruption(LocalTime startOfInterruption, Duration interruption)
        {
            return JavaFxUtil
                    .runOnFxApplicationThread(() -> showAutomaticInterruptionDialog(startOfInterruption, interruption));
        }

        private Boolean showAutomaticInterruptionDialog(LocalTime startOfInterruption, Duration interruption)
        {
            LOG.info("Showing automatic interruption alert starting at {} for {}...", startOfInterruption,
                    interruption);
            final Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Add automatic interruption?");
            alert.setHeaderText(
                    "An interruption of " + interruption + " was detected beginning at " + startOfInterruption + ".");
            final ButtonType addInterruption = new ButtonType("Add interruption", ButtonData.YES);
            final ButtonType skipInterruption = new ButtonType("Skip interruption", ButtonData.NO);
            final ButtonType stopWorkForToday = new ButtonType("Stop work for today", ButtonData.FINISH);
            alert.getButtonTypes().setAll(addInterruption, skipInterruption, stopWorkForToday);
            final Optional<ButtonType> selectedButton = alert.showAndWait();

            LOG.info("User clicked button {}", selectedButton);

            if (isButton(selectedButton, ButtonData.FINISH) && !stoppedWorkingForToday.get())
            {
                appService.toggleStopWorkForToday();
                return false;
            }
            return isButton(selectedButton, ButtonData.YES);
        }

        private boolean isButton(Optional<ButtonType> button, ButtonData data)
        {
            return button.map(ButtonType::getButtonData) //
                    .filter(d -> d == data) //
                    .isPresent();
        }

        @Override
        public void recordUpdated(DayRecord record)
        {
            JavaFxUtil.runOnFxApplicationThread(() -> {
                final YearMonth recordMonth = YearMonth.from(record.getDate());
                if (!availableMonths.isEmpty() && !availableMonths.contains(recordMonth))
                {
                    availableMonths.add(recordMonth);
                }
                if (currentMonth.get().getYearMonth().equals(recordMonth))
                {
                    currentMonth.setValue(record.getMonth());
                }
            });
        }

        @Override
        public void exceptionOccurred(Exception e)
        {
            showErrorDialog(e);
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

        @Override
        public void workStoppedForToday(boolean stopWorking)
        {
            JavaFxUtil.runOnFxApplicationThread(() -> stoppedWorkingForToday.setValue(stopWorking));
        }
    }
}
