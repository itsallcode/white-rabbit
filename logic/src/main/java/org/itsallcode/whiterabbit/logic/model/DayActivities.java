package org.itsallcode.whiterabbit.logic.model;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.json.JsonActivity;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;

public class DayActivities
{
    private static final Logger LOG = LogManager.getLogger(DayActivities.class);

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
        return getActivities()
                .map(wrapActivity())
                .collect(toList());
    }

    private Stream<JsonActivity> getActivities()
    {
        return Optional.ofNullable(day.getActivities())
                .orElse(emptyList())
                .stream();
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

    public void setRemainderActivity(JsonActivity activity, boolean remainder)
    {
        if (activity.isRemainder() == remainder)
        {
            LOG.debug("Remainder unchanged for {} / {}", activity, remainder);
            return;
        }
        final Duration unallocatedDuration = getUnallocatedDuration();
        if (!remainder)
        {
            activity.setDuration(unallocatedDuration);
            LOG.debug("Set unallocated time {} for {}", unallocatedDuration, activity);
            return;
        }

        final Optional<JsonActivity> currentRemainder = getRemainderActivity();
        if (currentRemainder.isPresent())
        {
            LOG.debug("Set unallocated time {} for other activity {}", unallocatedDuration, currentRemainder.get());
            currentRemainder.get().setDuration(unallocatedDuration);
        }
        activity.setDuration(null);
        LOG.debug("Make {} remainder activity", activity);
    }

    private Duration getUnallocatedDuration()
    {
        final Duration allocatedDuration = getActivities().map(JsonActivity::getDuration)
                .filter(d -> d != null)
                .reduce((d1, d2) -> d1.plus(d2)).orElse(Duration.ZERO);
        return dayRecord.getWorkingTime().minus(allocatedDuration);
    }

    public boolean isValidAllocation()
    {
        final List<JsonActivity> remainderActivities = getActivities()
                .filter(a -> a.getDuration() == null)
                .collect(toList());
        if (remainderActivities.size() >= 2)
        {
            LOG.warn("Found {} remainder activities for day {}: {}", remainderActivities.size(), dayRecord.getDate(),
                    remainderActivities);
            return false;
        }
        final Duration unallocatedDuration = getUnallocatedDuration();
        if (unallocatedDuration.isNegative())
        {
            LOG.warn("More working time allocated to activites than available: {}", unallocatedDuration.negated());
            return false;
        }
        if (remainderActivities.size() == 0 && !unallocatedDuration.isZero())
        {
            LOG.warn("No remainder activity but {} of working time is not allocated", unallocatedDuration);
            return false;
        }
        return true;
    }

    private Optional<JsonActivity> getRemainderActivity()
    {
        return getActivities().filter(a -> a.getDuration() == null).findFirst();
    }
}
