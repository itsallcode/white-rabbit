package org.itsallcode.whiterabbit.jfxui.testutil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.api.model.DayType;

public class DayTableExpectedRow implements TableRowExpectedContent
{
    private final LocalDate date;
    private final DayType dayType;
    private final LocalTime begin;
    private final LocalTime end;
    private final Duration breakDuration;
    private final Duration interruption;
    private final Duration workingTime;
    private final Duration overtimeToday;
    private final Duration totalOvertime;
    private final String comment;

    private DayTableExpectedRow(Builder builder)
    {
        this.date = builder.date;
        this.dayType = builder.dayType;
        this.begin = builder.begin;
        this.end = builder.end;
        this.breakDuration = builder.breakDuration;
        this.interruption = builder.interruption;
        this.workingTime = builder.workingTime;
        this.overtimeToday = builder.overtimeToday;
        this.totalOvertime = builder.totalOvertime;
        this.comment = builder.comment;
    }

    public static Builder defaultValues(LocalDate date, DayType dayType)
    {
        return builder().withDate(date).withDayType(dayType)
                .withBegin(null).withEnd(null)
                .withBreakDuration(Duration.ZERO)
                .withInterruption(Duration.ZERO).withWorkingTime(Duration.ZERO).withOvertimeToday(Duration.ZERO)
                .withTotalOvertime(Duration.ZERO).withComment(null);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @Override
    public Object[] expectedCellContent()
    {
        return new Object[] { date, dayType, begin, end, breakDuration, interruption, workingTime, overtimeToday,
                totalOvertime, comment };
    }

    public static Builder builderFrom(DayTableExpectedRow dayTableExpectedRow)
    {
        return new Builder(dayTableExpectedRow);
    }

    public static final class Builder
    {
        private LocalDate date;
        private DayType dayType;
        private LocalTime begin;
        private LocalTime end;
        private Duration breakDuration;
        private Duration interruption;
        private Duration workingTime;
        private Duration overtimeToday;
        private Duration totalOvertime;
        private String comment;

        private Builder()
        {
        }

        private Builder(DayTableExpectedRow dayTableExpectedRow)
        {
            this.date = dayTableExpectedRow.date;
            this.dayType = dayTableExpectedRow.dayType;
            this.begin = dayTableExpectedRow.begin;
            this.end = dayTableExpectedRow.end;
            this.breakDuration = dayTableExpectedRow.breakDuration;
            this.interruption = dayTableExpectedRow.interruption;
            this.workingTime = dayTableExpectedRow.workingTime;
            this.overtimeToday = dayTableExpectedRow.overtimeToday;
            this.totalOvertime = dayTableExpectedRow.totalOvertime;
            this.comment = dayTableExpectedRow.comment;
        }

        public Builder withDate(LocalDate date)
        {
            this.date = date;
            return this;
        }

        public Builder withDayType(DayType dayType)
        {
            this.dayType = dayType;
            return this;
        }

        public Builder withBegin(LocalTime begin)
        {
            this.begin = begin;
            return this;
        }

        public Builder withEnd(LocalTime end)
        {
            this.end = end;
            return this;
        }

        public Builder withBreakDuration(Duration breakDuration)
        {
            this.breakDuration = breakDuration;
            return this;
        }

        public Builder withInterruption(Duration interruption)
        {
            this.interruption = interruption;
            return this;
        }

        public Builder withWorkingTime(Duration workingTime)
        {
            this.workingTime = workingTime;
            return this;
        }

        public Builder withOvertimeToday(Duration overtimeToday)
        {
            this.overtimeToday = overtimeToday;
            return this;
        }

        public Builder withTotalOvertime(Duration totalOvertime)
        {
            this.totalOvertime = totalOvertime;
            return this;
        }

        public Builder withComment(String comment)
        {
            this.comment = comment;
            return this;
        }

        public DayTableExpectedRow build()
        {
            return new DayTableExpectedRow(this);
        }
    }
}
