package org.itsallcode.whiterabbit.jfxui;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import org.itsallcode.whiterabbit.jfxui.property.ClockPropertyFactory;
import org.itsallcode.whiterabbit.jfxui.property.ScheduledProperty;
import org.itsallcode.whiterabbit.jfxui.uistate.UiStateService;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.Interruption;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public final class AppState
{
    public final ObjectProperty<Interruption> interruption = new SimpleObjectProperty<>();
    public final ObjectProperty<MonthIndex> currentMonth = new SimpleObjectProperty<>();
    public final BooleanProperty stoppedWorkingForToday = new SimpleBooleanProperty(false);
    public final ObservableList<YearMonth> availableMonths = FXCollections.observableArrayList();

    public final SimpleObjectProperty<DayRecord> selectedDay = new SimpleObjectProperty<>(null);
    public final SimpleObjectProperty<Activity> selectedActivity = new SimpleObjectProperty<>(null);

    public final UiStateService uiState;

    public final ScheduledProperty<LocalDate> currentDateProperty;
    public final ScheduledProperty<Instant> currentTimeProperty;
    private Stage primaryStage;

    private AppState(ScheduledProperty<LocalDate> currentDateProperty, ScheduledProperty<Instant> currentTimeProperty,
            UiStateService uiState)
    {
        this.currentDateProperty = currentDateProperty;
        this.currentTimeProperty = currentTimeProperty;
        this.uiState = uiState;
    }

    static AppState create(AppService appService, UiStateService uiState)
    {
        final ClockPropertyFactory clockPropertyFactory = new ClockPropertyFactory(appService);
        return new AppState(clockPropertyFactory.currentDateProperty(), clockPropertyFactory.currentTimeProperty(),
                uiState);
    }

    public Optional<DayRecord> getSelectedDay()
    {
        return Optional.ofNullable(selectedDay.getValue());
    }

    public Optional<Activity> getSelectedActivity()
    {
        return Optional.ofNullable(selectedActivity.get());
    }

    public void setPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }

    public Optional<Stage> getPrimaryStage()
    {
        return Optional.ofNullable(primaryStage);
    }

    void shutdown()
    {
        currentDateProperty.cancel();
        currentTimeProperty.cancel();
        uiState.persistState();
    }
}
