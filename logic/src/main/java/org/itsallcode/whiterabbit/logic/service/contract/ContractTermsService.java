package org.itsallcode.whiterabbit.logic.service.contract;

import java.time.Duration;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

public class ContractTermsService
{
    private static final Duration WORKING_TIME_PER_DAY = Duration.ofHours(8);
    private static final Duration MIN_WORKING_TIME_WITHOUT_BREAK = Duration.ofHours(6);
    private static final Duration BASIC_BREAK = Duration.ofMinutes(45);

    public Duration getMandatoryBreak(DayRecord day)
    {
        if (!day.getType().isWorkDay())
        {
            return Duration.ZERO;
        }
        if (day.getRawWorkingTime().compareTo(MIN_WORKING_TIME_WITHOUT_BREAK) > 0)
        {
            return BASIC_BREAK;
        }
        return Duration.ZERO;
    }

    public Duration getMandatoryWorkingTime(DayRecord day)
    {
        if (day.isDummyDay())
        {
            return Duration.ZERO;
        }
        if (day.getType().isWorkDay())
        {
            return WORKING_TIME_PER_DAY;
        }
        return Duration.ZERO;
    }

    public Duration getWorkingTime(DayRecord day)
    {
        return day.getRawWorkingTime() //
                .minus(getMandatoryBreak(day)) //
                .minus(day.getInterruption());
    }

    public Duration getOvertime(DayRecord day)
    {
        return getWorkingTime(day) //
                .minus(getMandatoryWorkingTime(day));
    }

    public Duration getOverallOvertime(DayRecord day)
    {
        return getTotalOvertimeThisMonth(day).plus(getOvertimePreviousMonth(day));
    }

    private Duration getOvertimePreviousMonth(DayRecord day)
    {
        if (day.getMonth() != null && day.getMonth().getOvertimePreviousMonth() != null)
        {
            return day.getMonth().getOvertimePreviousMonth();
        }
        return Duration.ZERO;
    }

    public Duration getTotalOvertimeThisMonth(DayRecord day)
    {
        return day.getPreviousDayOvertime() //
                .plus(getOvertime(day));
    }
}
