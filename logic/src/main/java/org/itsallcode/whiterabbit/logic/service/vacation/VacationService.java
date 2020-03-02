package org.itsallcode.whiterabbit.logic.service.vacation;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReport.VacationMonth;
import org.itsallcode.whiterabbit.logic.service.vacation.VacationReport.VacationYear;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class VacationService
{
    private final Storage storage;
    private final AvailableVacationCalculator availableVacationCalculator;

    VacationService(Storage storage, AvailableVacationCalculator availableVacationCalculator)
    {
        this.storage = storage;
        this.availableVacationCalculator = availableVacationCalculator;
    }

    public VacationService(Storage storage)
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
        final List<YearMonth> availableDataYearMonth = storage.getAvailableDataYearMonth();
        final Map<YearMonth, MonthIndex> monthData = availableDataYearMonth.stream() //
                .map(storage::loadMonth) //
                .collect(toMap(MonthIndex::getYearMonth, Function.identity()));
        final Map<Year, Long> workingMonthCountByYear = availableDataYearMonth.stream() //
                .map(Year::from) //
                .collect(groupingBy(Function.identity(), counting()));
        final List<Year> years = workingMonthCountByYear.keySet().stream().sorted().collect(toList());

        private List<VacationYear> vacationDaysPerYear()
        {
            return years.stream().map(this::calculateVacation).collect(toList());
        }

        private VacationYear calculateVacation(final Year year)
        {
            return new VacationYear(year, vacationDaysCount(year), availableVacation(year), -1);
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
            // TODO Auto-generated method stub
            return null;
        }
    }
}
