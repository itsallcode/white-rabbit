package org.itsallcode.whiterabbit.jfxui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.feature.InterruptionPresetFeature;
import org.itsallcode.whiterabbit.jfxui.property.ScheduledProperty;
import org.itsallcode.whiterabbit.jfxui.table.activities.ActivitiesTable;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordTable;
import org.itsallcode.whiterabbit.jfxui.tray.Tray;
import org.itsallcode.whiterabbit.jfxui.tray.TrayCallback;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.itsallcode.whiterabbit.logic.service.Interruption;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class AppUi
{
    private static final int GAP_PIXEL = 10;

    private static final Logger LOG = LogManager.getLogger(AppUi.class);
    private final Locale locale;
    private final ObjectProperty<MonthIndex> currentMonth;
    private final ScheduledProperty<LocalDate> currentDateProperty;
    private final AppService appService;
    private final Stage primaryStage;

    private DayRecordTable dayRecordTable;
    private ActivitiesTable activitiesTable;
    private final BooleanProperty stoppedWorkingForToday;
    private final JavaFxApp app;
    private Tray tray;

    private final ObjectProperty<Interruption> interruption;

    private final ScheduledProperty<Instant> currentTimeProperty;

    private final ObservableList<YearMonth> availableMonths;

    AppUi(JavaFxApp app, Locale locale, ObjectProperty<Interruption> interruption,
            ObservableList<YearMonth> availableMonths, ObjectProperty<MonthIndex> currentMonth,
            ScheduledProperty<LocalDate> currentDateProperty, ScheduledProperty<Instant> currentTimeProperty,
            BooleanProperty stoppedWorkingForToday,
            AppService appService,
            Stage primaryStage)
    {
        this.app = app;
        this.locale = locale;
        this.interruption = interruption;
        this.availableMonths = availableMonths;
        this.currentMonth = currentMonth;
        this.currentDateProperty = currentDateProperty;
        this.currentTimeProperty = currentTimeProperty;
        this.stoppedWorkingForToday = stoppedWorkingForToday;
        this.appService = appService;
        this.primaryStage = primaryStage;
    }

    void createUi()
    {
        LOG.debug("Creating user interface");
        dayRecordTable = new DayRecordTable(locale, currentMonth,
                record -> appService.store(record),
                appService.formatter());

        activitiesTable = new ActivitiesTable(dayRecordTable.selectedDay(), record -> {
            appService.store(record);
            activitiesTable.refresh();
        }, appService.formatter(), appService.projects());
        final BorderPane rootPane = new BorderPane(createMainPane());
        rootPane.setTop(createTopContainer());
        final Scene scene = new Scene(rootPane, 780, 800);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F5)
            {
                appService.updateNow();
            }
        });

        primaryStage.setTitle("White Rabbit Time Recording " + appService.getAppProperties().getVersion());
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

    private VBox createTopContainer()
    {
        final MenuBar menuBar = new MenuBarBuilder(app, primaryStage, appService, this.stoppedWorkingForToday).build();
        final VBox topContainer = new VBox();
        topContainer.getChildren().addAll(menuBar, createToolBar());
        return topContainer;
    }

    private void createTrayIcon()
    {
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
                app.startManualInterruption();
            }

            @Override
            public void exit()
            {
                Platform.exit();
            }
        });

        if (!tray.isSupported())
        {
            LOG.trace("System tray is not supported: don't allow hiding primary stage");
            return;
        }
        LOG.trace("System tray is supported: allow hiding primary stage");
        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(event -> {
            LOG.trace("Hiding primary stage");
            event.consume();
            primaryStage.hide();
        });
    }

    private BorderPane createMainPane()
    {
        final Insets insets = new Insets(GAP_PIXEL);
        final Node daysTable = dayRecordTable.initTable();
        currentDateProperty.property()
                .addListener((observable, oldValue, newValue) -> dayRecordTable.selectRow(newValue));
        final Node activitiesTab = activitiesTable.initTable();
        final Button addActivityButton = button("add-activity-button", "+", "Add activity", e -> app.addActivity());
        final Button removeActivityButton = button("remove-activity-button", "-", "Remove activity",
                e -> app.removeActivity());
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

        BorderPane.setMargin(createStatusBar(), new Insets(0, GAP_PIXEL, 0, GAP_PIXEL));
        pane.setBottom(createStatusBar());

        return pane;
    }

    private ToolBar createToolBar()
    {
        final InterruptionPresetFeature interruptionPreset = new InterruptionPresetFeature(appService);

        final Button startInterruptionButton = button("start-interruption-button", "Start interruption",
                e -> app.startManualInterruption());
        startInterruptionButton.disableProperty().bind(interruption.isNotNull());

        return new ToolBar(monthDropDownBox(),
                new Separator(),
                startInterruptionButton,
                interruptionPreset.createButton(),
                createStopWorkForTodayButton(),
                new Separator(),
                button("update-button", "Update", e -> appService.updateNow()),
                new Separator(),
                button("vacation-report-button", "Vacation report", e -> app.showVacationReport()));
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

    private HBox createStatusBar()
    {
        final HBox status = new HBox();
        status.setPadding(new Insets(GAP_PIXEL));
        final Node left = overtimeLabel();
        final Pane spacer = new Pane();
        spacer.setMinSize(GAP_PIXEL, 1);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        final Node right = currentTimeLabel();
        status.getChildren().addAll(left, spacer, right);
        return status;
    }

    private Node currentTimeLabel()
    {
        final FormatterService formatter = appService.formatter();
        final Label label = new Label();
        label.setId("current-time-label");
        label.textProperty().bind(Bindings.createStringBinding(() -> {
            final Instant now = currentTimeProperty.property().getValue();
            return formatter.formatDateAndTime(now);
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
                return "Overtime previous month: " + formatter.format(month.getOvertimePreviousMonth())
                        + ", this month: "
                        + formatter.format(totalOvertime.minus(month.getOvertimePreviousMonth())) + ", total: "
                        + formatter.format(totalOvertime);
            }
            return "Overtime: (no month selected)";
        }, currentTimeProperty.property(), currentMonth));
        return label;
    }

    private Node monthDropDownBox()
    {
        availableMonths.addAll(appService.getAvailableDataYearMonth());
        final ComboBox<YearMonth> comboBox = new ComboBox<>(availableMonths);

        currentMonth.addListener(
                (observable, oldValue, newValue) -> comboBox.getSelectionModel().select(newValue.getYearMonth()));
        comboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> app.loadMonth(newValue));
        return comboBox;
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

    void bringWindowToFront()
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

    void shutdown()
    {
        if (tray != null)
        {
            tray.removeTrayIcon();
        }
    }

    public void selectDay(LocalDate date)
    {
        dayRecordTable.selectRow(date);
    }

    public Optional<DayRecord> getSelectedDay()
    {
        return Optional.ofNullable(dayRecordTable.selectedDay().getValue());
    }

    public Optional<Activity> getSelectedActivity()
    {
        return Optional.ofNullable(activitiesTable.selectedActivity().get());
    }

    public void updateActivities(DayRecord record)
    {
        activitiesTable.updateTableValues(record);
    }
}
