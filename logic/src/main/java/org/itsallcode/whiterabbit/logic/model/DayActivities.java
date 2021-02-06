package org.itsallcode.whiterabbit.logic.model;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.model.ActivityData;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.logic.model.json.JsonActivity;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

public class DayActivities
{
    private static final Logger LOG = LogManager.getLogger(DayActivities.class);

    private final ProjectService projectService;
    private final DayData day;
    final DayRecord dayRecord;

    DayActivities(DayRecord dayRecord, ProjectService projectService)
    {
        this.day = dayRecord.getJsonDay();
        this.dayRecord = dayRecord;
        this.projectService = Objects.requireNonNull(projectService);
    }

    public Activity add()
    {
        if (day.getActivities() == null)
        {
            day.setActivities(new ArrayList<>());
        }
        final boolean isFirstActivity = getActivities().findAny().isEmpty();

        final JsonActivity jsonActivity = new JsonActivity();
        jsonActivity.setProjectId("");
        jsonActivity.setDuration(Duration.ZERO);
        final int newRowIndex = day.getActivities().size();
        day.getActivities().add(jsonActivity);

        final Activity wrappedActivity = wrapActivity(jsonActivity, newRowIndex);
        wrappedActivity.setRemainderActivity(isFirstActivity);
        return wrappedActivity;
    }

    public List<Activity> getAll()
    {
        final List<ActivityData> jsonActivities = getActivities().collect(toList());
        return IntStream.range(0, jsonActivities.size())
                .mapToObj(i -> wrapActivity(jsonActivities.get(i), i))
                .collect(toList());
    }

    public boolean isEmpty()
    {
        return getActivities().map(a -> false)
                .findFirst().orElse(true);
    }

    private Stream<ActivityData> getActivities()
    {
        return Optional.ofNullable(day.getActivities())
                .orElse(emptyList())
                .stream();
    }

    private Activity wrapActivity(ActivityData activity, int index)
    {
        return new Activity(index, activity, this, projectService);
    }

    public Optional<Activity> get(int index)
    {
        return Optional.ofNullable(day.getActivities())
                .filter(list -> list.size() > index)
                .map(list -> list.get(index))
                .map(a -> wrapActivity(a, index));
    }

    public void remove(int index)
    {
        if (day.getActivities() == null)
        {
            return;
        }
        day.getActivities().remove(index);
        if (day.getActivities().isEmpty())
        {
            day.setActivities(null);
        }
    }

    public void setRemainderActivity(ActivityData activity, boolean remainder)
    {
        if (activity.isRemainder() == remainder)
        {
            LOG.debug("Remainder unchanged for {} / {}", activity, remainder);
            return;
        }
        if (!remainder)
        {
            final Duration unallocatedDuration = getUnallocatedDuration();
            activity.setDuration(unallocatedDuration);
            LOG.debug("Set unallocated time {} for {}", unallocatedDuration, activity);
            return;
        }

        final List<ActivityData> currentRemainders = getRemainderActivities();
        LOG.debug("Found {} remainder activities: deactivate them", currentRemainders.size());
        for (final ActivityData remainderActivity : currentRemainders)
        {
            final Duration unallocatedDuration = getUnallocatedDuration();
            remainderActivity.setDuration(unallocatedDuration);
            LOG.debug("Set unallocated time {} for other activity {}", unallocatedDuration, remainderActivity);
        }
        activity.setDuration(null);
        LOG.debug("Make {} remainder activity", activity);
    }

    private Duration getUnallocatedDuration()
    {
        final Duration allocatedDuration = getActivities().map(ActivityData::getDuration)
                .filter(Objects::nonNull)
                .reduce((d1, d2) -> d1.plus(d2))
                .orElse(Duration.ZERO);
        return dayRecord.getWorkingTime().minus(allocatedDuration);
    }

    public boolean isValidAllocation()
    {
        final List<ActivityData> remainderActivities = getActivities()
                .filter(a -> a.getDuration() == null)
                .collect(toList());
        if (remainderActivities.size() > 1)
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
        if (remainderActivities.isEmpty() && !unallocatedDuration.isZero())
        {
            LOG.warn("No remainder activity but {} of working time is not allocated", unallocatedDuration);
            return false;
        }
        return true;
    }

    private List<ActivityData> getRemainderActivities()
    {
        return getActivities().filter(a -> a.getDuration() == null).collect(toList());
    }

    public Duration getDuration(Activity activity)
    {
        if (activity.jsonActivity.getDuration() != null)
        {
            return activity.jsonActivity.getDuration();
        }
        return getUnallocatedDuration();
    }
}
