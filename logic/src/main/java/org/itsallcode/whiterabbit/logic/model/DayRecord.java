package org.itsallcode.whiterabbit.logic.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;

public class DayRecord
{
    private static final Logger LOG = LogManager.getLogger(DayRecord.class);

    private static final Duration BASIC_BREAK = Duration.ofMinutes(45);

    private final JsonDay day;
    private final Duration previousOvertime;

    public DayRecord(JsonDay day, Duration previousOvertime)
    {
        this.day = day;
        this.previousOvertime = Objects.requireNonNull(previousOvertime, "previousOvertime");
    }

    public Duration getMandatoryBreak()
    {
        if (!isWorkingDay())
        {
            return Duration.ZERO;
        }
        final Duration workingTime = getRawWorkingTime();
        if (workingTime.compareTo(Duration.ofHours(6)) > 0)
        {
            return BASIC_BREAK;
        }
        return Duration.ZERO;
    }

    public Duration getMandatoryWorkingTime()
    {
        if (day.isDummyDay())
        {
            return Duration.ZERO;
        }
        if (isWorkingDay())
        {
            return Duration.ofHours(8);
        }
        return Duration.ZERO;
    }

    private Duration getRawWorkingTime()
    {
        if (getBegin() == null && getEnd() == null)
        {
            return Duration.ZERO;
        }
        if (getBegin() == null || getEnd() == null)
        {
            LOG.warn("Either begin or end is missing for {}", this);
            return Duration.ZERO;
        }
        return Duration.between(getBegin(), getEnd());
    }

    public Duration getWorkingTime()
    {
        return getRawWorkingTime() //
                .minus(getMandatoryBreak()) //
                .minus(getInterruption());
    }

    public Duration getOvertime()
    {
        return getWorkingTime() //
                .minus(getMandatoryWorkingTime());
    }

    public Duration getTotalOvertime()
    {
        return previousOvertime.plus(getOvertime());
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

    public boolean isWorkingDay()
    {
        return getType().isWorkDay();
    }

    public LocalTime getBegin()
    {
        return day.getBegin();
    }

    public void setBegin(LocalTime begin)
    {
        setNonDummyDay();
        day.setBegin(begin);
    }

    public LocalTime getEnd()
    {
        return day.getEnd();
    }

    public void setEnd(LocalTime end)
    {
        setNonDummyDay();
        day.setEnd(end);
    }

    public Duration getInterruption()
    {
        return day.getInterruption() == null ? Duration.ZERO : day.getInterruption();
    }

    public void setInterruption(Duration interruption)
    {
        setNonDummyDay();
        day.setInterruption(interruption);
    }

    public JsonDay getJsonDay()
    {
        return day;
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

    public String getComment()
    {
        return day.getComment();
    }

    public void setComment(String comment)
    {
        setNonDummyDay();
        day.setComment(comment);
    }

    public void setType(DayType newValue)
    {
        setNonDummyDay();
        day.setType(newValue);
    }

    private void setNonDummyDay()
    {
        day.setDummyDay(false);
    }
}
