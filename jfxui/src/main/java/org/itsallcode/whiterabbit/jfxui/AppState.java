package org.itsallcode.whiterabbit.jfxui;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import org.itsallcode.whiterabbit.jfxui.property.ClockPropertyFactory;
import org.itsallcode.whiterabbit.jfxui.property.ScheduledProperty;
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

public final class AppState
{
    public final ObjectProperty<Interruption> interruption = new SimpleObjectProperty<>();
    public final ObjectProperty<MonthIndex> currentMonth = new SimpleObjectProperty<>();
    public final BooleanProperty stoppedWorkingForToday = new SimpleBooleanProperty(false);
    public final ObservableList<YearMonth> availableMonths = FXCollections.observableArrayList();

    public final SimpleObjectProperty<DayRecord> selectedDay = new SimpleObjectProperty<>(null);
    public final SimpleObjectProperty<Activity> selectedActivity = new SimpleObjectProperty<>(null);

    public final ScheduledProperty<YearMonth> currentMonthProperty;
    public final ScheduledProperty<LocalDate> currentDateProperty;
    public final ScheduledProperty<Instant> currentSecondProperty;

    private AppState(ScheduledProperty<YearMonth> currentMonthProperty,
            ScheduledProperty<LocalDate> currentDateProperty,
            ScheduledProperty<Instant> currentTimeProperty)
    {
        this.currentMonthProperty = currentMonthProperty;
        this.currentDateProperty = currentDateProperty;
        this.currentSecondProperty = currentTimeProperty;
    }

    static AppState create(AppService appService)
    {
        final ClockPropertyFactory clockPropertyFactory = new ClockPropertyFactory(appService);
        final ScheduledProperty<YearMonth> currentMonthProperty = clockPropertyFactory.currentMonthProperty();
        final ScheduledProperty<LocalDate> currentDateProperty = clockPropertyFactory.currentDateProperty();
        final ScheduledProperty<Instant> currentTimeProperty = clockPropertyFactory.currentInstantProperty();
        return new AppState(currentMonthProperty, currentDateProperty, currentTimeProperty);
    }

    public Optional<DayRecord> getSelectedDay()
    {
        return Optional.ofNullable(selectedDay.getValue());
    }

    public Optional<Activity> getSelectedActivity()
    {
        return Optional.ofNullable(selectedActivity.get());
    }

    void shutdown()
    {
        currentDateProperty.cancel();
        currentSecondProperty.cancel();
    }
}
