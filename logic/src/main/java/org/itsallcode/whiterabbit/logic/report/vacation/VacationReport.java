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
        private final YearMonth yearMonth;
        private final int vacationDaysUsed;

        VacationMonth(YearMonth yearMonth, int vacationDaysUsed)
        {
            this.yearMonth = yearMonth;
            this.vacationDaysUsed = vacationDaysUsed;
        }

        public YearMonth getYearMonth()
        {
            return yearMonth;
        }

        public int getVacationDaysUsed()
        {
            return vacationDaysUsed;
        }

        @Override
        public String toString()
        {
            return "VacationMonth [month=" + yearMonth + ", vacationDaysUsed=" + vacationDaysUsed + "]";
        }
    }

    public static class VacationYear
    {
        private final Year year;
        private final int daysUsed;
        private final int daysAvailable;
        private final int daysRemaingFromPreviousYear;

        VacationYear(Year year, int daysUsed, int daysAvailable, int daysRemaingFromPreviousYear)
        {
            this.year = year;
            this.daysUsed = daysUsed;
            this.daysAvailable = daysAvailable;
            this.daysRemaingFromPreviousYear = daysRemaingFromPreviousYear;
        }

        public Year getYear()
        {
            return year;
        }

        public int getDaysUsed()
        {
            return daysUsed;
        }

        public int getDaysAvailable()
        {
            return daysAvailable;
        }

        public int getDaysRemaingFromPreviousYear()
        {
            return daysRemaingFromPreviousYear;
        }

        public int getDaysRemaining()
        {
            return daysRemaingFromPreviousYear + daysAvailable - daysUsed;
        }

        @Override
        public String toString()
        {
            return "VacationYear [year=" + year + ", daysUsed=" + daysUsed + ", daysAvailable=" + daysAvailable
                    + ", daysRemaingFromPreviousYear=" + daysRemaingFromPreviousYear + "]";
        }

    }
}
