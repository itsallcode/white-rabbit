package org.itsallcode.whiterabbit.logic.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

public class DayRecord implements RowRecord
{
    private static final Logger LOG = LogManager.getLogger(DayRecord.class);

    private final ContractTermsService contractTerms;
    private final ProjectService projectService;
    private final ModelFactory modelFactory;
    private final DayData day;
    private final MonthIndex month;
    private final DayRecord previousDay;

    public DayRecord(final ContractTermsService contractTerms, final DayData day, final DayRecord previousDay,
            final MonthIndex month,
            final ProjectService projectService, final ModelFactory modelFactory)
    {
        this.contractTerms = contractTerms;
        this.projectService = Objects.requireNonNull(projectService);
        this.modelFactory = modelFactory;
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
        return getPreviousDayOvertime()
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
        final DayOfWeek dayOfWeek = day.getDate().getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public LocalTime getBegin()
    {
        return day.getBegin();
    }

    public void setBegin(final LocalTime begin)
    {
        day.setBegin(begin);
    }

    public LocalTime getEnd()
    {
        return day.getEnd();
    }

    public void setEnd(final LocalTime end)
    {
        day.setEnd(end);
    }

    public Duration getInterruption()
    {
        return day.getInterruption() == null ? Duration.ZERO : day.getInterruption();
    }

    public void setInterruption(final Duration interruption)
    {
        day.setInterruption(interruption.isZero() ? null : interruption);
    }

    DayData getJsonDay()
    {
        return day;
    }

    public String getComment()
    {
        return day.getComment();
    }

    public void setComment(final String comment)
    {
        day.setComment(comment.isEmpty() ? null : comment);
    }

    public void setType(final DayType type)
    {
        day.setType(Objects.requireNonNull(type, "type"));
    }

    public MonthIndex getMonth()
    {
        return month;
    }

    public boolean isDummyDay()
    {
        return day.getBegin() == null
                && day.getEnd() == null
                && day.getType() == null
                && day.getComment() == null
                && day.getInterruption() == null
                && (day.getActivities() == null
                        || day.getActivities().isEmpty());
    }

    public DayActivities activities()
    {
        return new DayActivities(this, projectService, modelFactory);
    }

    @Override
    public String toString()
    {
        return "DayRecord [day=" + day + "]";
    }

    @Override
    public int getRow()
    {
        return getDate().getDayOfMonth() - 1;
    }
}
