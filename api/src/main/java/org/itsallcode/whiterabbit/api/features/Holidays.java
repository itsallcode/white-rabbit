package org.itsallcode.whiterabbit.api.features;

import java.time.LocalDate;
import java.util.List;

public interface Holidays extends PluginFeature
{
    interface HolidayInstance
    {
        String getCategory();

        String getName();

        LocalDate getDate();
    }

    List<HolidayInstance> getHolidays(LocalDate date);
}
