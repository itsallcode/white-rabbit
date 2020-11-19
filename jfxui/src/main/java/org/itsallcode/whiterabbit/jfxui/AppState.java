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

class AppState
{
    final ObjectProperty<Interruption> interruption = new SimpleObjectProperty<>();
    final ObjectProperty<MonthIndex> currentMonth = new SimpleObjectProperty<>();
    final BooleanProperty stoppedWorkingForToday = new SimpleBooleanProperty(false);
    final ObservableList<YearMonth> availableMonths = FXCollections.observableArrayList();

    final ScheduledProperty<LocalDate> currentDateProperty;
    final ScheduledProperty<Instant> currentTimeProperty;

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

    public void shutdown()
    {
        currentDateProperty.cancel();
        currentTimeProperty.cancel();
    }
}
