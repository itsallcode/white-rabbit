package org.itsallcode.whiterabbit.logic.service.contract;

import java.time.Duration;

import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

public class ContractTermsService
{
    private static final Duration DEFAULT_MANDATORY_BREAK = Duration.ofMinutes(45);
    private static final Duration CONTRACTED_HOURS_PER_DAY = Duration.ofHours(8);
    private static final Duration MIN_WORKING_TIME_WITHOUT_BREAK = Duration.ofHours(6);

    private final Config config;

    public ContractTermsService(final Config config)
    {
        this.config = config;
    }


    public Duration getMandatoryBreak(final DayRecord day)
    {
        if (!day.getType().isWorkDay())
        {
            return Duration.ZERO;
        }
        final Duration workingTime = day.getRawWorkingTime().minus(day.getInterruption());
        if (workingTime.compareTo(MIN_WORKING_TIME_WITHOUT_BREAK) > 0)
        {
            return getMandatoryBreak();
        }
        return Duration.ZERO;
    }

    public Duration getMandatoryBreak()
    {
        return config.getMandatoryBreak().orElse(DEFAULT_MANDATORY_BREAK);
    }

    public Duration getContractedWorkingTimePerDay()
    {
        return CONTRACTED_HOURS_PER_DAY;
    }

    public Duration getCurrentWorkingTimePerDay()
    {
        return config.getCurrentHoursPerDay().orElse(getContractedWorkingTimePerDay());
    }

    public Duration getMandatoryWorkingTime(final DayRecord day)
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

    public Duration getWorkingTime(final DayRecord day)
    {
        return day.getRawWorkingTime() //
                .minus(getMandatoryBreak(day)) //
                .minus(day.getInterruption());
    }

    public Duration getOvertime(final DayRecord day)
    {
        return getWorkingTime(day) //
                .minus(getMandatoryWorkingTime(day));
    }

    public Duration getOverallOvertime(final DayRecord day)
    {
        return getTotalOvertimeThisMonth(day).plus(getOvertimePreviousMonth(day));
    }

    private Duration getOvertimePreviousMonth(final DayRecord day)
    {
        if (day.getMonth() != null && day.getMonth().getOvertimePreviousMonth() != null)
        {
            return day.getMonth().getOvertimePreviousMonth();
        }
        return Duration.ZERO;
    }

    public Duration getTotalOvertimeThisMonth(final DayRecord day)
    {
        return day.getPreviousDayOvertime() //
                .plus(getOvertime(day));
    }
}
