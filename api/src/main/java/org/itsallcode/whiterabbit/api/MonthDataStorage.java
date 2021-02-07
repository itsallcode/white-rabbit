package org.itsallcode.whiterabbit.api;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.api.model.ActivityData;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.api.model.MonthData;

public interface MonthDataStorage
{
    Optional<MonthData> load(YearMonth date);

    void store(YearMonth yearMonth, MonthData record);

    List<YearMonth> getAvailableDataMonths();

    List<MonthData> loadAll();

    ModelFactory getModelFactory();

    public interface ModelFactory
    {
        MonthData createMonthData();

        DayData createDayData();

        ActivityData createActivityData();
    }
}