package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import static java.util.stream.Collectors.joining;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.holidays.calculator.logic.Holiday;
import org.itsallcode.holidays.calculator.logic.HolidayService;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.DayType;

class HolidayAggregator
{
    private final ModelFactory factory;
    private final HolidayService service;
    private final List<DayData> dayData = new ArrayList<>();

    public HolidayAggregator(ModelFactory factory, HolidayService service)
    {
        this.factory = factory;
        this.service = service;
    }

    public List<DayData> getHolidays(YearMonth month)
    {
        for (int d = 1; d <= month.atEndOfMonth().getDayOfMonth(); d++)
        {
            process(factory, month.atDay(d));
        }
        return dayData;
    }

    private void process(ModelFactory factory, LocalDate date)
    {
        final List<Holiday> holidays = service.getHolidays(date);
        if (holidays.isEmpty())
        {
            return;
        }

        final DayData holiday = factory.createDayData();
        holiday.setDate(date);
        holiday.setType(DayType.HOLIDAY);
        final String comment = holidays.stream().map(Holiday::getName).collect(joining(","));
        holiday.setComment(comment);
        dayData.add(holiday);
    }
}
