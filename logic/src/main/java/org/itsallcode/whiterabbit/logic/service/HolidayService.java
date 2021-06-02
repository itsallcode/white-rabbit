package org.itsallcode.whiterabbit.logic.service;

import java.util.List;

import org.itsallcode.whiterabbit.api.features.HolidayProvider;

public class HolidayService
{
    private final HolidayProvider holidayProvider;

    // return all holidays of given year and month
    public HolidayService(List<HolidayProvider> providers)
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

}
