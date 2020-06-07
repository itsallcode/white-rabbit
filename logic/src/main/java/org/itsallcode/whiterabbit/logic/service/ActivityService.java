package org.itsallcode.whiterabbit.logic.service;

import java.time.LocalDate;
import java.time.YearMonth;

import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class ActivityService
{
    private final Storage storage;
    private final AppServiceCallback appServiceCallback;

    public ActivityService(Storage storage, AppServiceCallback appServiceCallback)
    {
        this.storage = storage;
        this.appServiceCallback = appServiceCallback;
    }

    public void addActivity(LocalDate date, String projectId)
    {
        final MonthIndex monthIndex = storage.loadMonth(YearMonth.from(date)).orElseThrow();
        final DayRecord day = monthIndex.getDay(date);

        day.addActivity(projectId);

        storage.storeMonth(monthIndex);
        appServiceCallback.recordUpdated(day);
    }

    public void updateActivity(int index, Activity activity)
    {
        final MonthIndex monthIndex = storage.loadMonth(YearMonth.from(activity.getDay().getDate())).orElseThrow();
        final DayRecord day = monthIndex.getDay(activity.getDay().getDate());

        final Activity activityToUpdate = day.getActivity(index)
                .orElseThrow(() -> new IllegalArgumentException("No activity with index " + index + " found"));

        activityToUpdate.updateValuesFrom(activity);

        storage.storeMonth(monthIndex);
        appServiceCallback.recordUpdated(day);
    }

    public void removeActivity(int index, LocalDate date)
    {
        final MonthIndex monthIndex = storage.loadMonth(YearMonth.from(date)).orElseThrow();
        final DayRecord day = monthIndex.getDay(date);

        day.removeActivity(index);

        storage.storeMonth(monthIndex);
        appServiceCallback.recordUpdated(day);
    }
}
