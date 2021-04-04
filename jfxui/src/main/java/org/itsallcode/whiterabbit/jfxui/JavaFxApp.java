package org.itsallcode.whiterabbit.jfxui;

import java.lang.ProcessHandle.Info;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.property.DelayedPropertyListener;
import org.itsallcode.whiterabbit.jfxui.splashscreen.ProgressPreloaderNotification;
import org.itsallcode.whiterabbit.jfxui.splashscreen.ProgressPreloaderNotification.Type;
import org.itsallcode.whiterabbit.jfxui.ui.AppUi;
import org.itsallcode.whiterabbit.jfxui.ui.InterruptionDialog;
import org.itsallcode.whiterabbit.jfxui.uistate.UiStateService;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.ConfigLoader;
import org.itsallcode.whiterabbit.logic.DefaultWorkingDirProvider;
import org.itsallcode.whiterabbit.logic.WorkingDirProvider;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.AppServiceCallback;
import org.itsallcode.whiterabbit.logic.service.singleinstance.OtherInstance;
import org.itsallcode.whiterabbit.logic.service.singleinstance.RunningInstanceCallback;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class JavaFxApp extends Application
{
    private static final Logger LOG = LogManager.getLogger(JavaFxApp.class);
    private static final String MESSAGE_BRING_TO_FRONT = "bringToFront";
    private static final String MESSAGE_GET_PROCESS_INFO = "getProcessInfo";

    private AppService appService;

    private Stage primaryStage;

    private final WorkingDirProvider workingDirProvider;
    private final Clock clock;
    private final ScheduledExecutorService scheduledExecutor;

    private AppState state;
    private UiActions actions;
    private AppUi ui;

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
        final Config config = loadConfig();
        this.appService = AppService.create(config, clock, scheduledExecutor);
        LOG.info("Starting white-rabbit version {}", appService.getAppProperties().getVersion());
        final Optional<OtherInstance> otherInstance = appService
                .registerSingleInstance(this::messageFromOtherInstanceReceived);
        if (otherInstance.isPresent())
        {
            final String response = otherInstance.get().sendMessageWithResponse(MESSAGE_GET_PROCESS_INFO);
            otherInstance.get().sendMessage(MESSAGE_BRING_TO_FRONT);
            throw new OtherInstanceAlreadyRunningException(response);
        }

        state = AppState.create(appService,
                UiStateService.loadState(config, new DelayedPropertyListener(appService.scheduler())));
        actions = UiActions.create(config, state, appService, getHostServices());
    }

    private Config loadConfig()
    {
        final ConfigLoader configLoader = new ConfigLoader(workingDirProvider);
        return Optional.ofNullable(getParameters().getNamed().get("config"))
                .map(Paths::get)
                .map(configLoader::loadConfig)
                .orElseGet(configLoader::loadConfigFromDefaultLocations);
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        state.setPrimaryStage(primaryStage);
        LOG.info("Starting UI");
        doStart(primaryStage);
        notifyPreloaderProgress(Type.STARTUP_FINISHED);
    }

    private void doStart(Stage primaryStage)
    {
        this.ui = new AppUi.Builder(this, actions, appService, primaryStage, state).build();

        primaryStage.show();

        startAppService();

        loadInitialData();
    }

    private void loadInitialData()
    {
        final YearMonth thisMonth = appService.getClock().getCurrentYearMonth();
        loadMonth(thisMonth);
        ui.selectDay(appService.getClock().getCurrentDate());
    }

    public void loadMonth(final YearMonth month)
    {
        final MonthIndex record = appService.getOrCreateMonth(month);
        state.currentMonth.setValue(record);
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
        if (ui != null)
        {
            ui.shutdown();
            ui = null;
        }

        if (state != null)
        {
            state.shutdown();
            state = null;
        }

        if (appService != null)
        {
            appService.close();
            appService = null;
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

    public void bringWindowToFront()
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

    public void addActivity()
    {
        final Optional<DayRecord> selectedDay = state.getSelectedDay();
        if (selectedDay.isEmpty())
        {
            LOG.warn("No day selected, can't add an activity");
            return;
        }
        appService.activities().addActivity(selectedDay.get().getDate());
    }

    public void removeActivity()
    {
        final Optional<Activity> selectedActivity = state.getSelectedActivity();
        if (selectedActivity.isEmpty())
        {
            LOG.info("No activity selected to be removed");
            return;
        }
        appService.activities().removeActivity(selectedActivity.get());
    }

    public void startManualInterruption()
    {
        if (state.interruption.isNotNull().get())
        {
            LOG.warn("Interruption {} already active", state.interruption.get());
            return;
        }
        JavaFxUtil.runOnFxApplicationThread(() -> {
            state.interruption.set(appService.startInterruption());
            new InterruptionDialog(primaryStage, state.currentTimeProperty.property(), state.interruption, clock)
                    .show();
        });
    }

    private final class AppServiceCallbackImplementation implements AppServiceCallback
    {
        @Override
        public InterruptionDetectedDecision automaticInterruptionDetected(LocalTime startOfInterruption,
                Duration interruption)
        {
            return JavaFxUtil
                    .runOnFxApplicationThread(() -> showAutomaticInterruptionDialog(startOfInterruption, interruption));
        }

        private InterruptionDetectedDecision showAutomaticInterruptionDialog(LocalTime startOfInterruption,
                Duration interruption)
        {
            LOG.info("Showing automatic interruption alert starting at {} for {}...", startOfInterruption,
                    interruption);
            final Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Interruption detected");
            alert.initModality(Modality.NONE);
            alert.initOwner(primaryStage);
            alert.setHeaderText(
                    "An interruption of " + interruption + " was detected beginning at " + startOfInterruption + ".");
            final ButtonType addInterruption = new ButtonType("Add interruption", ButtonData.YES);
            final ButtonType skipInterruption = new ButtonType("Skip interruption", ButtonData.NO);
            final ButtonType stopWorkForToday = new ButtonType("Stop working for today", ButtonData.FINISH);
            alert.getButtonTypes().setAll(addInterruption, skipInterruption, stopWorkForToday);
            final Optional<ButtonType> selectedButton = alert.showAndWait();

            final InterruptionDetectedDecision decision = evaluateButton(selectedButton);
            LOG.info("User clicked button {} -> {}", selectedButton, decision);
            return decision;
        }

        private InterruptionDetectedDecision evaluateButton(final Optional<ButtonType> selectedButton)
        {
            if (isButton(selectedButton, ButtonData.FINISH) && !state.stoppedWorkingForToday.get())
            {
                return InterruptionDetectedDecision.STOP_WORKING_FOR_TODAY;
            }
            if (isButton(selectedButton, ButtonData.YES))
            {
                return InterruptionDetectedDecision.ADD_INTERRUPTION;
            }
            return InterruptionDetectedDecision.SKIP_INTERRUPTION;
        }

        private boolean isButton(Optional<ButtonType> button, ButtonData data)
        {
            return button.map(ButtonType::getButtonData)
                    .filter(d -> d == data)
                    .isPresent();
        }

        @Override
        public void recordUpdated(DayRecord record)
        {
            JavaFxUtil.runOnFxApplicationThread(() -> {
                final YearMonth recordMonth = YearMonth.from(record.getDate());
                if (!state.availableMonths.isEmpty() && !state.availableMonths.contains(recordMonth))
                {
                    state.availableMonths.add(recordMonth);
                }
                if (state.currentMonth.get().getYearMonth().equals(recordMonth))
                {
                    state.currentMonth.setValue(record.getMonth());
                    if (daySelected(record))
                    {
                        ui.updateActivities(record);
                    }
                }
            });
        }

        private boolean daySelected(DayRecord record)
        {
            final Optional<DayRecord> selectedDay = state.getSelectedDay();
            return selectedDay.isPresent() && selectedDay.get().getDate().equals(record.getDate());
        }

        @Override
        public void exceptionOccurred(Exception e)
        {
            final String message = "An error occured: " + e.getClass() + ": " + e.getMessage();
            LOG.error(message, e);
            actions.showErrorDialog(message);
        }

        @Override
        public void workStoppedForToday(boolean stopWorking)
        {
            JavaFxUtil.runOnFxApplicationThread(() -> state.stoppedWorkingForToday.setValue(stopWorking));
        }
    }
}
