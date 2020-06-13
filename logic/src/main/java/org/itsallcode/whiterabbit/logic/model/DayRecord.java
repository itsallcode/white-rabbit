package org.itsallcode.whiterabbit.logic.model;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.model.json.JsonActivity;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;

public class DayRecord
{
    private static final Logger LOG = LogManager.getLogger(DayRecord.class);

    private final ContractTermsService contractTerms;
    private final JsonDay day;
    private final MonthIndex month;
    private final DayRecord previousDay;

    public DayRecord(ContractTermsService contractTerms, JsonDay day, DayRecord previousDay, MonthIndex month)
    {
        this.contractTerms = contractTerms;
        this.day = day;
        this.previousDay = previousDay;
        this.month = month;
    }

    public Duration getMandatoryBreak()
    {
        return contractTerms.getMandatoryBreak(this);
    }

    public Duration getMandatoryWorkingTime()
    {
        return contractTerms.getMandatoryWorkingTime(this);
    }

    public Optional<Duration> getCustomWorkingTime()
    {
        return Optional.ofNullable(day.getWorkingHours());
    }

    public Duration getRawWorkingTime()
    {
        if (getBegin() == null && getEnd() == null)
        {
            return Duration.ZERO;
        }
        if (getBegin() == null || getEnd() == null)
        {
            LOG.trace("Either begin or end is missing for {}", this);
            return Duration.ZERO;
        }
        return Duration.between(getBegin(), getEnd());
    }

    public Duration getWorkingTime()
    {
        return contractTerms.getWorkingTime(this);
    }

    public Duration getOvertime()
    {
        return contractTerms.getOvertime(this);
    }

    public Duration getOverallOvertime()
    {
        return contractTerms.getOverallOvertime(this);
    }

    public Duration getTotalOvertimeThisMonth()
    {
        return getPreviousDayOvertime() //
                .plus(getOvertime());
    }

    public Duration getPreviousDayOvertime()
    {
        return previousDay != null ? previousDay.getTotalOvertimeThisMonth() : Duration.ZERO;
    }

    public LocalDate getDate()
    {
        return day.getDate();
    }

    public DayType getType()
    {
        if (day.getType() != null)
        {
            return day.getType();
        }
        if (isWeekend())
        {
            return DayType.WEEKEND;
        }
        return DayType.WORK;
    }

    private boolean isWeekend()
    {
        switch (day.getDate().getDayOfWeek())
        {
        case SATURDAY:
        case SUNDAY:
            return true;
        default:
            return false;
        }
    }

    public LocalTime getBegin()
    {
        return day.getBegin();
    }

    public void setBegin(LocalTime begin)
    {
        day.setBegin(Objects.requireNonNull(begin, "begin"));
    }

    public LocalTime getEnd()
    {
        return day.getEnd();
    }

    public void setEnd(LocalTime end)
    {
        day.setEnd(Objects.requireNonNull(end, "end"));
    }

    public Duration getInterruption()
    {
        return day.getInterruption() == null ? Duration.ZERO : day.getInterruption();
    }

    public void setInterruption(Duration interruption)
    {
        day.setInterruption(interruption.isZero() ? null : interruption);
    }

    JsonDay getJsonDay()
    {
        return day;
    }

    public String getComment()
    {
        return day.getComment();
    }

    public void setComment(String comment)
    {
        day.setComment(comment.isEmpty() ? null : comment);
    }

    public void setType(DayType type)
    {
        day.setType(Objects.requireNonNull(type, "type"));
    }

    public MonthIndex getMonth()
    {
        return month;
    }

    public boolean isDummyDay()
    {
        return day.getBegin() == null && day.getEnd() == null //
                && day.getType() == null && day.getComment() == null && day.getInterruption() == null;
    }

    public Activity addActivity(String projectId)
    {
        if (day.getActivities() == null)
        {
            day.setActivities(new ArrayList<>());
        }
        final JsonActivity jsonActivity = new JsonActivity(projectId);
        day.getActivities().add(jsonActivity);
        return new Activity(jsonActivity, this);
    }

    public List<Activity> getActivities()
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

    public Optional<Activity> getActivity(int index)
    {
        return Optional.ofNullable(day.getActivities())
                .filter(list -> list.size() > index)
                .map(list -> list.get(index))
                .map(wrapActivity());
    }

    public void removeActivity(int index)
    {
        if (day.getActivities() == null)
        {
            return;
        }
        day.getActivities().remove(index);
    }

    @Override
    public String toString()
    {
        return "DayRecord [day=" + day + "]";
    }

}
