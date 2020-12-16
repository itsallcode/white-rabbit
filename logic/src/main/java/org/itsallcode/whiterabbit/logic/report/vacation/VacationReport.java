package org.itsallcode.whiterabbit.logic.report.vacation;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public class VacationReport
{
    public final List<VacationMonth> months;
    public final List<VacationYear> years;

    VacationReport(List<VacationMonth> months, List<VacationYear> years)
    {
        this.months = months;
        this.years = years;
    }

    public static class VacationMonth
    {
        VacationMonth(YearMonth yearMonth, int vacationDaysUsed)
        {
            this.yearMonth = yearMonth;
            this.vacationDaysUsed = vacationDaysUsed;
        }

        public final YearMonth yearMonth;
        public final int vacationDaysUsed;

        @Override
        public String toString()
        {
            return "VacationMonth [month=" + yearMonth + ", vacationDaysUsed=" + vacationDaysUsed + "]";
        }
    }

    public static class VacationYear
    {
        public final Year year;
        public final int daysUsed;
        public final int daysAvailable;
        public final int daysRemaingFromPreviousYear;

        VacationYear(Year year, int daysUsed, int daysAvailable, int daysRemaingFromPreviousYear)
        {
            this.year = year;
            this.daysUsed = daysUsed;
            this.daysAvailable = daysAvailable;
            this.daysRemaingFromPreviousYear = daysRemaingFromPreviousYear;
        }

        @Override
        public String toString()
        {
            return "VacationYear [year=" + year + ", daysUsed=" + daysUsed + ", daysAvailable=" + daysAvailable
                    + ", daysRemaingFromPreviousYear=" + daysRemaingFromPreviousYear + "]";
        }

        public int getDaysRemaining()
        {
            return daysRemaingFromPreviousYear + daysAvailable - daysUsed;
        }
    }
}
