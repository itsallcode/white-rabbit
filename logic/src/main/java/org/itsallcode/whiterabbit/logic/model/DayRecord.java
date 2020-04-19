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
    private final MonthIndex month;
    private final DayRecord previousDay;

    public DayRecord(JsonDay day, DayRecord previousDay, MonthIndex month)
    {
        this.day = day;
        this.previousDay = previousDay;
        this.month = month;
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
        if (isDummyDay())
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
            LOG.trace("Either begin or end is missing for {}", this);
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

    public Duration getOverallOvertime()
    {
        return getTotalOvertimeThisMonth().plus(getOvertimePreviousMonth());
    }

    private Duration getOvertimePreviousMonth()
    {
        if (month != null && month.getOvertimePreviousMonth() != null)
        {
            return month.getOvertimePreviousMonth();
        }
        return Duration.ZERO;
    }

    public Duration getTotalOvertimeThisMonth()
    {
        return getPreviousDayOvertime() //
                .plus(getOvertime());
    }

    private Duration getPreviousDayOvertime()
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
        day.setInterruption(Objects.requireNonNull(interruption, "interruption"));
    }

    JsonDay getJsonDay()
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
        day.setComment(comment);
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

    @Override
    public String toString()
    {
        return "DayRecord [day=" + day + "]";
    }
}
