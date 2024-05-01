package org.itsallcode.whiterabbit.logic.report.vacation;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationMonth;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport.VacationYear;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class VacationReportGenerator
{
    private final Storage storage;
    private final AvailableVacationCalculator availableVacationCalculator;

    VacationReportGenerator(final Storage storage, final AvailableVacationCalculator availableVacationCalculator)
    {
        this.storage = storage;
        this.availableVacationCalculator = availableVacationCalculator;
    }

    public VacationReportGenerator(final Storage storage)
    {
        this(storage, new AvailableVacationCalculator());
    }

    public VacationReport generateReport()
    {
        final Calculator calculator = new Calculator();
        return new VacationReport(calculator.vacationDaysPerMonth(), calculator.vacationDaysPerYear());
    }

    private class Calculator
    {
        final List<YearMonth> availableDataYearMonth = storage.getAvailableDataMonths();
        final Map<YearMonth, MonthIndex> monthData = availableDataYearMonth.stream() //
                .map(storage::loadMonth) //
                .map(Optional::orElseThrow) //
                .collect(toMap(MonthIndex::getYearMonth, Function.identity()));
        final Map<Year, Long> workingMonthCountByYear = availableDataYearMonth.stream() //
                .map(Year::from) //
                .collect(groupingBy(Function.identity(), counting()));
        final List<Year> years = workingMonthCountByYear.keySet().stream().sorted().toList();

        private List<VacationYear> vacationDaysPerYear()
        {
            final List<VacationYear> vacationYears = new ArrayList<>(years.size());
            VacationYear previousVacationYear = null;
            for (final Year year : years)
            {
                final VacationYear vacationYear = calculateVacation(year, previousVacationYear);
                vacationYears.add(vacationYear);
                previousVacationYear = vacationYear;
            }
            return vacationYears;
        }

        private VacationYear calculateVacation(final Year year, final VacationYear previousYear)
        {
            final int daysRemainingFromPreviousYear = previousYear != null ? previousYear.getDaysRemaining() : 0;
            return new VacationYear(year, vacationDaysCount(year), availableVacation(year),
                    daysRemainingFromPreviousYear);
        }

        private int vacationDaysCount(final Year year)
        {
            return monthData.values().stream() //
                    .filter(month -> month.getYearMonth().getYear() == year.getValue()) //
                    .mapToInt(MonthIndex::getVacationDayCount) //
                    .sum();
        }

        private int availableVacation(final Year year)
        {
            if (year.equals(years.get(0)))
            {
                final long workingMonthCount = workingMonthCountByYear.get(year);
                return availableVacationCalculator.getVacationDaysFirstYear(workingMonthCount);
            }
            return availableVacationCalculator.getVacationDaysPerYear();
        }

        public List<VacationMonth> vacationDaysPerMonth()
        {
            return monthData.values().stream() //
                    .sorted(comparing(MonthIndex::getYearMonth)) //
                    .map(this::calculateMonthVacation) //
                    .toList();
        }

        private VacationMonth calculateMonthVacation(final MonthIndex month)
        {
            return new VacationMonth(month.getYearMonth(), month.getVacationDays());
        }
    }
}
