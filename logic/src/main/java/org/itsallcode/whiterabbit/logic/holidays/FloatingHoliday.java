package org.itsallcode.whiterabbit.logic.holidays;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class FloatingHoliday extends Holiday
{
    private final int month;
    private final int day;
    private final int offset;
    private final DayOfWeek dayOfWeek;

    public FloatingHoliday(String name, int offset, DayOfWeek dayOfWeek, int month)
    {
        this(name, offset, dayOfWeek, month, -1);
    }

    /**
     * Holiday called name on the offset dayOfWeek after/before month day.
     * 
     * <p>
     * Examples
     * 
     * @param name
     * @param offset
     *            If offset > 0, use the offset dayOfWeek after month day. If
     *            offset < 0, use the offset dayOfWeek before month day.
     * @param dayOfWeek
     *            0 means Sunday, 1 means Monday, and so on.
     * @param month
     * @param day
     *            Number of pivot day in given month. If day < 1 then use
     *            default. If offset > 0 then default = 1 other wise default =
     *            month's last day.
     */
    public FloatingHoliday(String name, int offset, DayOfWeek dayOfWeek, int month, int day)
    {
        super(name);
        this.month = month;
        this.day = (offset >= 0 && day < 1) ? 1 : day;
        this.offset = offset;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public LocalDate of(int year)
    {
        final LocalDate pivotDay = pivotDay(year).with(TemporalAdjusters.previousOrSame(dayOfWeek));
        final int delta = (offset < 0 ? +1 : -1);
        return pivotDay.plusDays(7 * (offset + delta));
    }

    private LocalDate pivotDay(int year)
    {
        if (offset > 0)
        {
            return LocalDate.of(year, month, day).plusDays(6);
        }

        if (day < 1)
        {
            return LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
        }

        return LocalDate.of(year, month, day);
    }

}
