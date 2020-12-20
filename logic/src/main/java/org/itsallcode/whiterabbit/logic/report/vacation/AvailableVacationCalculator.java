package org.itsallcode.whiterabbit.logic.report.vacation;

class AvailableVacationCalculator
{
    private static final int MONTHS_PER_YEAR = 12;
    private static final int VACATION_DAYS_PER_YEAR = 30;

    int getVacationDaysFirstYear(long workingMonthCount)
    {
        final double ratio = (workingMonthCount * 1D) / MONTHS_PER_YEAR;
        return (int) Math.ceil(ratio * getVacationDaysPerYear());
    }

    int getVacationDaysPerYear()
    {
        return VACATION_DAYS_PER_YEAR;
    }
}
