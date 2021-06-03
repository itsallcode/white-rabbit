package org.itsallcode.whiterabbit.logic.service;

import java.time.YearMonth;
import java.util.List;

import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;

public class HolidayService
{
    private final Holidays holidayProvider;

    public HolidayService(List<Holidays> providers)
    {
        // how to select provider?
        if (providers.isEmpty())
        {
            this.holidayProvider = null;
        }
        else
        {
            this.holidayProvider = providers.get(0);
        }
    }

    public List<DayData> getHolidays(ModelFactory factory, YearMonth month)
    {
        return holidayProvider.getHolidays(factory, month);
    }

}
