package org.itsallcode.whiterabbit.logic.service.contract;

import java.time.Duration;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

public class ContractTermsService
{
    public static final Duration BASIC_BREAK = Duration.ofMinutes(45);
    private static final Duration CONTRACTED_HOURS_PER_DAY = Duration.ofHours(8);
    private static final Duration MIN_WORKING_TIME_WITHOUT_BREAK = Duration.ofHours(6);

    private final Optional<Duration> hoursPerDay;

    public ContractTermsService(Config config)
    {
        this(config.getCurrentHoursPerDay());
    }

    public ContractTermsService(Optional<Duration> hoursPerDay)
    {
        this.hoursPerDay = hoursPerDay;
    }

    public Duration getMandatoryBreak(DayRecord day)
    {
        if (!day.getType().isWorkDay())
        {
            return Duration.ZERO;
        }
        final Duration workingTime = day.getRawWorkingTime().minus(day.getInterruption());
        if (workingTime.compareTo(MIN_WORKING_TIME_WITHOUT_BREAK) > 0)
        {
            return BASIC_BREAK;
        }
        return Duration.ZERO;
    }

    public Duration getContractedWorkingTimePerDay()
    {
        return CONTRACTED_HOURS_PER_DAY;
    }

    public Duration getCurrentWorkingTimePerDay()
    {
        return hoursPerDay.orElse(getContractedWorkingTimePerDay());
    }

    public Duration getMandatoryWorkingTime(DayRecord day)
    {
        if (day.isDummyDay())
        {
            return Duration.ZERO;
        }
        if (day.getType().isWorkDay())
        {
            return day.getCustomWorkingTime().orElse(getContractedWorkingTimePerDay());
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
