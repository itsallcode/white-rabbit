package org.itsallcode.whiterabbit.jfxui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.ProcessHandle.Info;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.property.ClockPropertyFactory;
import org.itsallcode.whiterabbit.jfxui.property.ScheduledProperty;
import org.itsallcode.whiterabbit.jfxui.splashscreen.ProgressPreloaderNotification;
import org.itsallcode.whiterabbit.jfxui.splashscreen.ProgressPreloaderNotification.Type;
import org.itsallcode.whiterabbit.jfxui.table.activities.ActivitiesTable;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordTable;
import org.itsallcode.whiterabbit.jfxui.tray.Tray;
import org.itsallcode.whiterabbit.jfxui.tray.TrayCallback;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.DefaultWorkingDirProvider;
import org.itsallcode.whiterabbit.logic.WorkingDirProvider;
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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
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

    private Tray tray;
    private Locale locale;

    private final WorkingDirProvider workingDirProvider;
    private final Clock clock;
    private final ScheduledExecutorService scheduledExecutor;

    public JavaFxApp()
    {
        this(new DefaultWorkingDirProvider(), Clock.systemDefaultZone(), new ScheduledThreadPoolExecutor(1));
    }

    JavaFxApp(WorkingDirProvider workingDirProvider, Clock clock, ScheduledExecutorService executorService)
    {
        this.workingDirProvider = workingDirProvider;
        this.clock = clock;
        this.scheduledExecutor = executorService;
    }

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
        final Config config = Config.read(workingDirProvider);
        this.locale = config.getLocale();
        this.appService = AppService.create(config, clock, scheduledExecutor);
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
        prepareShutdown();
        Platform.exit();
    }

    void prepareShutdown()
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
        }, appService.formatter());

        activitiesTable = new ActivitiesTable(dayRecordTable.selectedDay(), record -> {
            appService.store(record);
            activitiesTable.refresh();
        }, appService.formatter(), appService.projects());
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
        try (InputStream resourceStream = JavaFxApp.class.getResourceAsStream("/icon.png"))
        {
            primaryStage.getIcons().add(new Image(resourceStream));
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error loading image from resource", e);
        }

        createTrayIcon();

        primaryStage.setScene(scene);
        LOG.debug("User interface finished");
    }

    private void createTrayIcon()
    {
        if (!tray.isSupported())
        {
            LOG.debug("System tray is not supported: don't allow hiding primary stage");
            return;
        }
        LOG.trace("System tray is supported: allow hiding primary stage");
        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(event -> {
            LOG.debug("Hiding primary stage");
            event.consume();
            primaryStage.hide();
        });
    }

    private BorderPane createMainPane()
    {
        final Insets insets = new Insets(GAP_PIXEL);
        final Node daysTable = dayRecordTable.initTable();
        final Node activitiesTab = activitiesTable.initTable();
        final Button addActivityButton = button("add-activity-button", "+", "Add activity", e -> addActivity());
        final Button removeActivityButton = button("remove-activity-button", "-", "Remove activity",
                e -> removeActivity());
        final VBox activitiesButtonPane = new VBox(GAP_PIXEL,
                addActivityButton,
                removeActivityButton);
        final SplitPane mainPane = new SplitPane(daysTable,
                new TitledPane("Activities", new HBox(GAP_PIXEL, activitiesButtonPane, activitiesTab)));
        HBox.setHgrow(activitiesTab, Priority.ALWAYS);
        mainPane.setOrientation(Orientation.VERTICAL);
        mainPane.setDividerPositions(0.8);

        final BorderPane pane = new BorderPane();
        pane.setCenter(mainPane);

        BorderPane.setMargin(mainPane, insets);

        final FlowPane topPane = createTopPane();
        pane.setTop(topPane);
        BorderPane.setMargin(topPane, new Insets(GAP_PIXEL, GAP_PIXEL, 0, GAP_PIXEL));

        final TilePane buttonBar = createButtonBar();
        BorderPane.setMargin(buttonBar, insets);
        pane.setBottom(buttonBar);

        return pane;
    }

    private TilePane createButtonBar()
    {
        final Button startInterruptionButton = button("start-interruption-button", "Start interruption",
                e -> startManualInterruption());
        startInterruptionButton.disableProperty().bind(interruption.isNotNull());
        final TilePane buttonPane = new TilePane(Orientation.HORIZONTAL);
        buttonPane.setHgap(GAP_PIXEL);

        buttonPane.getChildren().addAll(button("update-button", "Update", e -> appService.updateNow()),
                startInterruptionButton,
                createStopWorkForTodayButton(),
                button("vacation-report-button", "Vacation report", e -> showVacationReport()));
        return buttonPane;
    }

    private void addActivity()
    {
        final DayRecord selectedDay = dayRecordTable.selectedDay().getValue();
        if (selectedDay == null)
        {
            return;
        }
        appService.activities().addActivity(selectedDay.getDate());
    }

    private void removeActivity()
    {
        if (activitiesTable.selectedActivity().get() == null)
        {
            LOG.info("No activity selected to be removed");
            return;
        }
        appService.activities().removeActivity(activitiesTable.selectedActivity().get());
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

    private FlowPane createTopPane()
    {
        final FlowPane topPane = new FlowPane();
        topPane.setHgap(GAP_PIXEL);
        topPane.getChildren().add(new Label("Month:"));
        topPane.getChildren().add(monthDropDownBox());
        topPane.getChildren().add(currentTimeLabel());
        topPane.getChildren().add(overtimeLabel());
        return topPane;
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
        final FormatterService formatter = appService.formatter();
        final Label label = new Label();
        label.setId("current-time-label");
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            final Instant now = currentTimeProperty.property().getValue();
            return "Current time: " + formatter.formatDateAndTime(now);
        }, currentTimeProperty.property()));
        return label;
    }

    private Node overtimeLabel()
    {
        final FormatterService formatter = appService.formatter();
        final Label label = new Label();
        label.setId("overtime-label");
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            final MonthIndex month = currentMonth.get();
            if (month != null && month.getOvertimePreviousMonth() != null)
            {
                final Duration totalOvertime = month.getTotalOvertime();
                return "Overtime: previous month: " + formatter.format(month.getOvertimePreviousMonth())
                        + ", this month: "
                        + formatter.format(totalOvertime.minus(month.getOvertimePreviousMonth())) + ", total: "
                        + formatter.format(totalOvertime);
            }
            return "Overtime: (no month selected)";
        }, currentTimeProperty.property(), currentMonth));
        return label;
    }

    private Button button(String id, String label, EventHandler<ActionEvent> action)
    {
        return button(id, label, null, action);
    }

    private Button button(String id, String label, String tooltip, EventHandler<ActionEvent> action)
    {
        final Button button = new Button(label);
        button.setId(id);
        button.setOnAction(action);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        if (tooltip != null)
        {
            button.setTooltip(new Tooltip(tooltip));
        }
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
                    if (daySelected(record))
                    {
                        activitiesTable.updateTableValues(record);
                    }
                }
            });
        }

        private boolean daySelected(DayRecord record)
        {
            final DayRecord selectedDay = dayRecordTable.selectedDay().getValue();
            return selectedDay != null && selectedDay.getDate().equals(record.getDate());
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
