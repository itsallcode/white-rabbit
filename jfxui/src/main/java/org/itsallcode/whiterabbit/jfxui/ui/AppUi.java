package org.itsallcode.whiterabbit.jfxui.ui;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.AppState;
import org.itsallcode.whiterabbit.jfxui.JavaFxApp;
import org.itsallcode.whiterabbit.jfxui.UiActions;
import org.itsallcode.whiterabbit.jfxui.feature.InterruptionPresetFeature;
import org.itsallcode.whiterabbit.jfxui.table.activities.ActivitiesTable;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordTable;
import org.itsallcode.whiterabbit.jfxui.tray.Tray;
import org.itsallcode.whiterabbit.jfxui.tray.TrayCallback;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppUi
{
    private static final Logger LOG = LogManager.getLogger(AppUi.class);

    private final DayRecordTable dayRecordTable;
    private final ActivitiesTable activitiesTable;
    private final Tray tray;

    private AppUi(Builder builder)
    {
        dayRecordTable = builder.dayRecordTable;
        activitiesTable = builder.activitiesTable;
        tray = builder.tray;
    }

    public void shutdown()
    {
        tray.removeTrayIcon();
    }

    public void selectDay(LocalDate date)
    {
        dayRecordTable.selectRow(date);
    }

    public void updateActivities(DayRecord record)
    {
        activitiesTable.updateTableValues(record);
    }

    public static class Builder
    {
        private static final double GAP_PIXEL = UiResources.GAP_PIXEL;

        private final Stage primaryStage;
        private final AppService appService;
        private final Locale locale;
        private final JavaFxApp app;
        private final AppState state;
        private final UiActions actions;

        private DayRecordTable dayRecordTable;
        private ActivitiesTable activitiesTable;
        private Tray tray;

        public Builder(JavaFxApp app, UiActions actions, AppService appService, Stage primaryStage, AppState appState,
                Locale locale)
        {
            this.app = app;
            this.actions = actions;
            this.locale = locale;
            this.state = appState;
            this.appService = appService;
            this.primaryStage = primaryStage;
        }

        public AppUi build()
        {
            LOG.debug("Creating user interface");
            dayRecordTable = new DayRecordTable(locale, state.selectedDay, state.currentMonth, record -> {
                appService.store(record);
                if (record.getDate().equals(state.getSelectedDay().map(DayRecord::getDate).orElse(null)))
                {
                    LOG.debug("Current day {} updated: refresh activities", record.getDate());
                    activitiesTable.refresh();
                }
            }, appService.formatter(), appService.autocomplete());

            activitiesTable = new ActivitiesTable(state.selectedDay, state.selectedActivity, record -> {
                appService.store(record);
                activitiesTable.refresh();
            }, appService.formatter(), appService.projects(), appService.autocomplete());
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
            primaryStage.getIcons().add(UiResources.APP_ICON);

            createTrayIcon();

            primaryStage.setScene(scene);
            primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
                if (event.getCode() == KeyCode.ESCAPE)
                {
                    primaryStage.hide();
                }
            });
            LOG.debug("User interface finished");
            return new AppUi(this);
        }

        private VBox createTopContainer()
        {
            final MenuBar menuBar = new MenuBarBuilder(actions, primaryStage, appService, state.stoppedWorkingForToday)
                    .build();
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
                    app.bringWindowToFront();
                }

                @Override
                public void startInterruption()
                {
                    app.startManualInterruption();
                }

                @Override
                public void exit()
                {
                    actions.exitApp();
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
            state.currentDateProperty.property().addListener(this::dateChanged);
            final Node activitiesTab = activitiesTable.initTable();
            final Button addActivityButton = UiWidget.button("add-activity-button", "+", "Add activity",
                    e -> app.addActivity());
            final Button removeActivityButton = UiWidget.button("remove-activity-button", "-", "Remove activity",
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

            final Button startInterruptionButton = UiWidget.button("start-interruption-button", "Start interruption",
                    e -> app.startManualInterruption());
            startInterruptionButton.disableProperty().bind(state.interruption.isNotNull());

            return new ToolBar(monthDropDownBox(),
                    new Separator(),
                    startInterruptionButton,
                    interruptionPreset.createButton(),
                    createStopWorkForTodayButton(),
                    new Separator(),
                    UiWidget.button("update-button", "Update", e -> appService.updateNow()));
        }

        private Button createStopWorkForTodayButton()
        {
            final Button button = new Button();
            button.setId("start-stop-working-button");
            button.textProperty()
                    .bind(Bindings.createStringBinding(
                            () -> state.stoppedWorkingForToday.get() ? "Continue working" : "Stop working for today",
                            state.stoppedWorkingForToday));
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
                final Instant now = state.currentTimeProperty.property().getValue();
                return formatter.formatDateAndTime(now);
            }, state.currentTimeProperty.property()));
            return label;
        }

        private Node overtimeLabel()
        {
            final FormatterService formatter = appService.formatter();
            final Label label = new Label();
            label.setId("overtime-label");
            label.textProperty().bind(Bindings.createStringBinding(() -> {
                final MonthIndex month = state.currentMonth.get();
                if (month != null && month.getOvertimePreviousMonth() != null)
                {
                    final Duration totalOvertime = month.getTotalOvertime();
                    return "Overtime previous month: " + formatter.format(month.getOvertimePreviousMonth())
                            + ", this month: "
                            + formatter.format(totalOvertime.minus(month.getOvertimePreviousMonth())) + ", total: "
                            + formatter.format(totalOvertime);
                }
                return "Overtime: (no month selected)";
            }, state.currentTimeProperty.property(), state.currentMonth));
            return label;
        }

        private Node monthDropDownBox()
        {
            state.availableMonths.addAll(appService.getAvailableDataYearMonth());
            final ComboBox<YearMonth> comboBox = new ComboBox<>(state.availableMonths);
            comboBox.setId("selected-month-combobox");

            state.currentMonth.addListener(
                    (observable, oldValue, newValue) -> comboBox.getSelectionModel().select(newValue.getYearMonth()));
            comboBox.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> app.loadMonth(newValue));
            return comboBox;
        }

        private void dateChanged(ObservableValue<? extends LocalDate> observable, LocalDate oldDate, LocalDate newDate)
        {
            dayRecordTable.selectRow(newDate);
            if (oldDate.getMonth() != newDate.getMonth())
            {
                app.loadMonth(YearMonth.from(newDate));
            }
        }
    }
}
