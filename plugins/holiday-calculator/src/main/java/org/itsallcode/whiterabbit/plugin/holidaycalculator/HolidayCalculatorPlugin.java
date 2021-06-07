package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import org.itsallcode.whiterabbit.api.AbstractPlugin;

public class HolidayCalculatorPlugin extends AbstractPlugin<CalculatedHolidays>
{
    public HolidayCalculatorPlugin()
    {
        super("holidaycalculator", CalculatedHolidays.class);
    }

    @Override
    protected CalculatedHolidays createInstance()
    {
        return new CalculatedHolidays(config.getDataDir());
    }
}
