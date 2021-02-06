package org.itsallcode.whiterabbit.logic.storage.data;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.itsallcode.whiterabbit.logic.model.json.MonthData;

public interface MonthDataStorage
{
    Optional<JsonMonth> load(YearMonth date);

    void store(YearMonth yearMonth, MonthData record);

    List<YearMonth> getAvailableDataMonths();

    List<JsonMonth> loadAll();
}