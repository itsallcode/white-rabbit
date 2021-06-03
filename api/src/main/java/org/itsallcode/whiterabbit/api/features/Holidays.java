package org.itsallcode.whiterabbit.api.features;

import java.time.YearMonth;
import java.util.List;

import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.DayData;

public interface Holidays extends PluginFeature
{
    List<DayData> getHolidays(ModelFactory factory, YearMonth month);
}
