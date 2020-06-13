package org.itsallcode.whiterabbit.logic.model;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.itsallcode.whiterabbit.logic.model.json.JsonActivity;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;

public class DayActivities
{
    private final JsonDay day;
    final DayRecord dayRecord;

    public DayActivities(JsonDay jsonDay, DayRecord dayRecord)
    {
        this.day = jsonDay;
        this.dayRecord = dayRecord;
    }

    public Activity add(String projectId)
    {
        if (day.getActivities() == null)
        {
            day.setActivities(new ArrayList<>());
        }
        final JsonActivity jsonActivity = new JsonActivity(projectId);
        day.getActivities().add(jsonActivity);
        return new Activity(jsonActivity, this);
    }

    public List<Activity> getAll()
    {
        return Optional.ofNullable(day.getActivities())
                .orElse(emptyList())
                .stream().map(wrapActivity())
                .collect(toList());
    }

    private Function<JsonActivity, Activity> wrapActivity()
    {
        return a -> new Activity(a, this);
    }

    public Optional<Activity> get(int index)
    {
        return Optional.ofNullable(day.getActivities())
                .filter(list -> list.size() > index)
                .map(list -> list.get(index))
                .map(wrapActivity());
    }

    public void remove(int index)
    {
        if (day.getActivities() == null)
        {
            return;
        }
        day.getActivities().remove(index);
    }

}
