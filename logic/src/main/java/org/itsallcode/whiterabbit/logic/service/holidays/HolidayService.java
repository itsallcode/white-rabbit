package org.itsallcode.whiterabbit.logic.service.holidays;

import java.time.YearMonth;
import java.util.List;

import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;

public class HolidayService
{
    private final List<Holidays> holidayProviders;

    public HolidayService(List<Holidays> providers)
    {
        this.holidayProviders = providers;
    }

    public List<DayData> getHolidays(ModelFactory factory, YearMonth month)
    {
        final HolidayAggregator aggregator = new HolidayAggregator();
        for (final Holidays provider : holidayProviders)
        {
            aggregator.collect(provider, month);
        }
        return aggregator.createDayData(factory);
    }

}
