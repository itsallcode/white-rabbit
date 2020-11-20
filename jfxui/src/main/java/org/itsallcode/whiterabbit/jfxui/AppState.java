package org.itsallcode.whiterabbit.jfxui;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;

import org.itsallcode.whiterabbit.jfxui.property.ClockPropertyFactory;
import org.itsallcode.whiterabbit.jfxui.property.ScheduledProperty;
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

    public final ScheduledProperty<LocalDate> currentDateProperty;
    public final ScheduledProperty<Instant> currentTimeProperty;

    private AppState(ScheduledProperty<LocalDate> currentDateProperty, ScheduledProperty<Instant> currentTimeProperty)
    {
        this.currentDateProperty = currentDateProperty;
        this.currentTimeProperty = currentTimeProperty;
    }

    static AppState create(AppService appService)
    {
        final ClockPropertyFactory clockPropertyFactory = new ClockPropertyFactory(appService);
        return new AppState(clockPropertyFactory.currentDateProperty(), clockPropertyFactory.currentTimeProperty());
    }

    void shutdown()
    {
        currentDateProperty.cancel();
        currentTimeProperty.cancel();
    }
}