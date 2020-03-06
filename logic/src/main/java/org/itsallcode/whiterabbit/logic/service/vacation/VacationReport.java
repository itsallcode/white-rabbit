package org.itsallcode.whiterabbit.logic.service.vacation;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public class VacationReport
{

    public final List<VacationMonth> months;
    public final List<VacationYear> years;

    public VacationReport(List<VacationMonth> months, List<VacationYear> years)
    {
        this.months = months;
        this.years = years;
    }

    public static class VacationMonth
    {
        public VacationMonth(YearMonth yearMonth, int vacationDaysUsed)
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
        public VacationYear(Year year, int daysUsed, int daysAvailable, int daysRemaingFromLastYear)
        {
            this.year = year;
            this.daysUsed = daysUsed;
            this.daysAvailable = daysAvailable;
            this.daysRemaingFromLastYear = daysRemaingFromLastYear;
        }

        public final Year year;
        public final int daysUsed;
        public final int daysAvailable;
        public final int daysRemaingFromLastYear;

        @Override
        public String toString()
        {
            return "VacationYear [year=" + year + ", daysUsed=" + daysUsed + ", daysAvailable=" + daysAvailable
                    + ", daysRemaingFromLastYear=" + daysRemaingFromLastYear + "]";
        }

        public int getDaysRemaining()
        {
            return daysRemaingFromLastYear + daysAvailable - daysUsed;
        }
    }
}
