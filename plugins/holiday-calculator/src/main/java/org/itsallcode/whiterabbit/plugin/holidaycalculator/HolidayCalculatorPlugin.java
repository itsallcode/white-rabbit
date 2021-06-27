package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import org.itsallcode.whiterabbit.api.AbstractPlugin;
import org.itsallcode.whiterabbit.api.features.Holidays;

public class HolidayCalculatorPlugin extends AbstractPlugin<Holidays>
{
    public HolidayCalculatorPlugin()
    {
        super("holidaycalculator", Holidays.class);
    }

    @Override
    protected CalculatedHolidays createInstance()
    {
        return new CalculatedHolidays(config.getDataDir());
    }
}
