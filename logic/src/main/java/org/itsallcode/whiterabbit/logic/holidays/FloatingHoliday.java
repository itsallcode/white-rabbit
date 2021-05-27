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
        this("holiday", name, offset, dayOfWeek, month, -1);
    }

    /**
     * Holiday called &lt;name&gt; on the &lt;offset&gt; &lt;dayOfWeek&gt;
     * after/before &lt;month&gt; &lt;day>.
     * 
     * <p>
     * Examples
     * 
     * @param category
     *            Arbitrary category that may be evaluated by the application
     *            processng the holiday.
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
    public FloatingHoliday(String category, String name, int offset, DayOfWeek dayOfWeek, int month, int day)
    {
        super(category, name);
        this.month = month;
        this.day = (offset >= 0 && day < 1) ? 1 : day;
        this.offset = offset;
        this.dayOfWeek = dayOfWeek;
        if (this.day > 0)
        {
            ensureValidDate(month, this.day);
        }
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

    @Override
    public String toString()
    {
        return String.format("%s(%s %s: %d %s %02d-%02d)",
                this.getClass().getSimpleName(), getCategory(), getName(), offset, dayOfWeek, month, day);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + day;
        result = prime * result + ((dayOfWeek == null) ? 0 : dayOfWeek.hashCode());
        result = prime * result + month;
        result = prime * result + offset;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        final FloatingHoliday other = (FloatingHoliday) obj;
        // negative days are rated as equal
        if ((day > 1 || other.day > 0) && day != other.day)
        {
            return false;
        }
        if (dayOfWeek != other.dayOfWeek)
        {
            return false;
        }
        if (month != other.month)
        {
            return false;
        }
        if (offset != other.offset)
        {
            return false;
        }
        return true;
    }

}
