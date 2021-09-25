package org.itsallcode.whiterabbit.jfxui.ui;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.AppState;
import org.itsallcode.whiterabbit.jfxui.JavaFxApp;
import org.itsallcode.whiterabbit.jfxui.UiActions;
import org.itsallcode.whiterabbit.jfxui.feature.InterruptionPresetFeature;
import org.itsallcode.whiterabbit.jfxui.table.activities.ActivitiesTable;
import org.itsallcode.whiterabbit.jfxui.table.activities.ActivityPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordTable;
import org.itsallcode.whiterabbit.jfxui.tray.Tray;
import org.itsallcode.whiterabbit.jfxui.tray.TrayCallback;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.DayOfWeekWithoutDotFormatter;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.TableView;
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

    public void updateActivities(DayRecord dayRecord)
    {
        activitiesTable.updateTableValues(dayRecord);
    }

    public static class Builder
    {
        private final Stage primaryStage;
        private final AppService appService;
        private final JavaFxApp app;
        private final AppState state;
        private final UiActions actions;
        private final DayOfWeekWithoutDotFormatter shortDateFormatter;

        private DayRecordTable dayRecordTable;
        private ActivitiesTable activitiesTable;
        private Tray tray;

        public Builder(JavaFxApp app, UiActions actions, AppService appService, Stage primaryStage, AppState appState)
        {
            this.app = app;
            this.actions = actions;
            this.state = appState;
            this.appService = appService;
            this.primaryStage = primaryStage;
            this.shortDateFormatter = appService.formatter().getCustomShortDateFormatter();
        }

        public AppUi build()
        {
            LOG.debug("Creating user interface");
            dayRecordTable = new DayRecordTable(state.selectedDay, state.currentMonth, dayRecord -> {
                appService.store(dayRecord);
                if (dayRecord.getDate().equals(state.getSelectedDay().map(DayRecord::getDate).orElse(null)))
                {
                    LOG.debug("Current day {} updated: refresh activities", dayRecord.getDate());
                    activitiesTable.refresh();
                }
            }, appService);

            activitiesTable = new ActivitiesTable(state.selectedDay, state.selectedActivity, dayRecord -> {
                appService.store(dayRecord);
                activitiesTable.refresh();
            }, appService);
            final BorderPane rootPane = new BorderPane(createMainPane());
            rootPane.setTop(createTopContainer());
            final Scene scene = UiWidget.scene(rootPane, 780, 800);
            scene.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.F5)
                {
                    keyEvent.consume();
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
                    event.consume();
                    primaryStage.hide();
                }
            });

            state.uiState.register("main-window", primaryStage);
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
            final TableView<DayRecordPropertyAdapter> daysTable = dayRecordTable.initTable();
            state.currentDateProperty.property().addListener(this::dateChanged);
            final TableView<ActivityPropertyAdapter> activitiesTab = activitiesTable.initTable();
            final Button addActivityButton = UiWidget.button("add-activity-button", "+", "Add activity",
                    e -> app.addActivity());
            final Button removeActivityButton = UiWidget.button("remove-activity-button", "-", "Remove activity",
                    e -> app.removeActivity());
            final VBox activitiesButtonPane = new VBox(UiResources.GAP_PIXEL,
                    addActivityButton,
                    removeActivityButton);
            final TitledPane titledPane = new TitledPane("Activities",
                    new HBox(UiResources.GAP_PIXEL, activitiesButtonPane, activitiesTab));
            titledPane.setId("activities-titled-pane");
            titledPane.setCollapsible(true);
            titledPane.textProperty().bind(activitiesPaneTitle());
            final SplitPane mainPane = new SplitPane(daysTable, titledPane);
            HBox.setHgrow(activitiesTab, Priority.ALWAYS);
            mainPane.setId("mainSplitPane");
            mainPane.setOrientation(Orientation.VERTICAL);
            mainPane.setDividerPositions(0.8);

            final BorderPane pane = new BorderPane();
            pane.setCenter(mainPane);

            BorderPane.setMargin(mainPane, UiResources.DEFAULT_MARGIN);

            BorderPane.setMargin(createStatusBar(), new Insets(0, UiResources.GAP_PIXEL, 0, UiResources.GAP_PIXEL));
            pane.setBottom(createStatusBar());

            state.uiState.register(daysTable);
            state.uiState.register(activitiesTab);
            state.uiState.register(titledPane);
            state.uiState.register(mainPane);
            return pane;
        }

        private StringBinding activitiesPaneTitle()
        {
            final SimpleObjectProperty<DayRecord> selectedDay = state.selectedDay;
            final Property<LocalDate> currentDateProperty = state.currentDateProperty.property();
            return Bindings.createStringBinding(() -> {
                String title = "Activities";
                if (selectedDay.get() == null)
                {
                    return title;
                }
                final LocalDate date = selectedDay.get().getDate();
                if (date.equals(currentDateProperty.getValue()))
                {
                    title += " today";
                }
                else
                {
                    title += " on " + shortDateFormatter.format(date);
                }
                return title;
            }, selectedDay, currentDateProperty);
        }

        private ToolBar createToolBar()
        {
            final InterruptionPresetFeature interruptionPreset = new InterruptionPresetFeature(appService);

            final Button startInterruptionButton = UiWidget.button("start-interruption-button", "Start interruption",
                    e -> app.startManualInterruption());
            startInterruptionButton.disableProperty().bind(state.interruption.isNotNull());

            return new ToolBar(
                    UiWidget.button("previous-month-button", "<", "Select previous month", e -> gotoMonth(-1)),
                    monthDropDownBox(),
                    UiWidget.button("next-month-button", ">", "Select next month", e -> gotoMonth(+1)),
                    new Separator(),
                    startInterruptionButton,
                    interruptionPreset.createButton(),
                    createStopWorkForTodayButton(),
                    new Separator(),
                    UiWidget.button("update-button", "Update", e -> appService.updateNow()));
        }

        private void gotoMonth(int step)
        {
            final YearMonth selecteMonth = state.currentMonth.get().getYearMonth().plusMonths(step);
            app.loadMonth(selecteMonth);
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
            status.setPadding(UiResources.DEFAULT_MARGIN);
            final Node left = overtimeLabel();
            final Pane spacer = new Pane();
            spacer.setMinSize(UiResources.GAP_PIXEL, 1);
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
