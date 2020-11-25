package org.itsallcode.whiterabbit.logic.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.autocomplete.AutocompleteService;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class ActivityService
{
    private final Storage storage;
    private final AppServiceCallback appServiceCallback;
    private final AutocompleteService autocompleteService;

    public ActivityService(Storage storage, AutocompleteService autocompleteService,
            AppServiceCallback appServiceCallback)
    {
        this.storage = storage;
        this.autocompleteService = autocompleteService;
        this.appServiceCallback = appServiceCallback;
    }

    public void addActivity(LocalDate date)
    {
        final MonthIndex monthIndex = storage.loadMonth(YearMonth.from(date)).orElseThrow();
        final DayRecord day = monthIndex.getDay(date);

        final Activity newActivity = day.activities().add();
        final Optional<Project> suggestedProject = autocompleteService.getSuggestedProject();
        if (suggestedProject.isPresent())
        {
            newActivity.setProject(suggestedProject.get());
        }

        storage.storeMonth(monthIndex);
        appServiceCallback.recordUpdated(day);
    }

    public void removeActivity(Activity activity)
    {
        removeActivity(activity.getDay().getDate(), activity.getRow());
    }

    private void removeActivity(LocalDate date, int index)
    {
        final MonthIndex monthIndex = storage.loadMonth(YearMonth.from(date)).orElseThrow();
        final DayRecord day = monthIndex.getDay(date);

        day.activities().remove(index);

        storage.storeMonth(monthIndex);
        appServiceCallback.recordUpdated(day);
    }
}
