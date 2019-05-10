package org.itsallcode.whiterabbit.logic.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;

public class DayRecord
{
    private static final Duration BASIC_BREAK = Duration.ofMinutes(45);

    private final JsonDay day;

    public DayRecord(JsonDay day)
    {
        this.day = day;
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
        if (isWorkingDay())
        {
            return Duration.ofHours(8);
        }
        else
        {
            return Duration.ZERO;
        }
    }

    private Duration getRawWorkingTime()
    {
        if (getBegin() == null && getEnd() == null)
        {
            return Duration.ZERO;
        }
        if (getBegin() == null || getEnd() == null)
        {
            throw new IllegalStateException("Begin or end is null for " + day);
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
        day.setBegin(begin);
    }

    public LocalTime getEnd()
    {
        return day.getEnd();
    }

    public void setEnd(LocalTime end)
    {
        day.setEnd(end);
    }

    public Duration getInterruption()
    {
        return day.getInterruption() == null ? Duration.ZERO : day.getInterruption();
    }

    public void setInterruption(Duration interruption)
    {
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
}
