package org.itsallcode.whiterabbit.logic.service;

import java.time.LocalDate;
import java.time.YearMonth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class ActivityService
{
    private static final Logger LOG = LogManager.getLogger(ActivityService.class);

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

        day.activities().add(projectId);

        storage.storeMonth(monthIndex);
        appServiceCallback.recordUpdated(day);
    }

    private void updateActivity(Activity activity)
    {
        final MonthIndex monthIndex = storage.loadMonth(YearMonth.from(activity.getDay().getDate())).orElseThrow();
        final DayRecord day = monthIndex.getDay(activity.getDay().getDate());

        final Activity activityToUpdate = day.activities().get(activity.getRow())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No activity with index " + activity.getRow() + " found for day " + day.getDate()));

        activityToUpdate.updateValuesFrom(activity);

        storage.storeMonth(monthIndex);
        appServiceCallback.recordUpdated(day);
    }

    public void removeActivity(int index, LocalDate date)
    {
        final MonthIndex monthIndex = storage.loadMonth(YearMonth.from(date)).orElseThrow();
        final DayRecord day = monthIndex.getDay(date);

        day.activities().remove(index);

        storage.storeMonth(monthIndex);
        appServiceCallback.recordUpdated(day);
    }
}
