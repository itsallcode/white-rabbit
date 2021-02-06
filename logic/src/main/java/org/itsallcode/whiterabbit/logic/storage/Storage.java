package org.itsallcode.whiterabbit.logic.storage;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;

public interface Storage
{
    Optional<MonthIndex> loadMonth(YearMonth date);

    MonthIndex loadOrCreate(YearMonth yearMonth);

    void storeMonth(MonthIndex month);

    MultiMonthIndex loadAll();

    List<YearMonth> getAvailableDataMonths();

}