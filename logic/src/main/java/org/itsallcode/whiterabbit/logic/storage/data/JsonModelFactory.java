package org.itsallcode.whiterabbit.logic.storage.data;

import org.itsallcode.whiterabbit.api.features.MonthDataStorage.ModelFactory;
import org.itsallcode.whiterabbit.api.model.ActivityData;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.MonthData;

public class JsonModelFactory implements ModelFactory
{
    @Override
    public MonthData createMonthData()
    {
        return new JsonMonth();
    }

    @Override
    public DayData createDayData()
    {
        return new JsonDay();
    }

    @Override
    public ActivityData createActivityData()
    {
        return new JsonActivity();
    }
}
