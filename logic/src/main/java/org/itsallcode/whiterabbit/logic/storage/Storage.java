package org.itsallcode.whiterabbit.logic.storage;

import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.MultiMonthIndex;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

public class Storage
{
    private final StorageLoadingListener loadingListener;
    private final MonthIndexStorage monthIndexStorage;

    Storage(MonthIndexStorage monthIndexStorage, StorageLoadingListener loadingListener)
    {
        this.monthIndexStorage = monthIndexStorage;
        this.loadingListener = loadingListener;
    }

    public static Storage create(Path dataDir, ContractTermsService contractTerms,
            ProjectService projectService, StorageLoadingListener loadingListener)
    {
        final DateToFileMapper dateToFileMapper = new DateToFileMapper(dataDir);
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        final JsonFileStorage fileStorage = new JsonFileStorage(jsonb, dateToFileMapper);
        final MonthIndexStorage monthIndexStorage = new MonthIndexStorage(contractTerms, projectService, fileStorage);
        return new Storage(monthIndexStorage, loadingListener);
    }

    public Optional<MonthIndex> loadMonth(YearMonth date)
    {
        return monthIndexStorage.loadMonth(date).map(this::updateCache);
    }

    public MonthIndex loadOrCreate(final YearMonth yearMonth)
    {
        return updateCache(monthIndexStorage.loadOrCreate(yearMonth));
    }

    public void storeMonth(MonthIndex month)
    {
        monthIndexStorage.storeMonth(updateCache(month));
    }

    public MultiMonthIndex loadAll()
    {
        return updateCache(monthIndexStorage.loadAll());
    }

    private MultiMonthIndex updateCache(MultiMonthIndex index)
    {
        index.getMonths().forEach(this::updateCache);
        return index;
    }

    private MonthIndex updateCache(MonthIndex month)
    {
        loadingListener.monthLoaded(month);
        return month;
    }

    public List<YearMonth> getAvailableDataYearMonth()
    {
        return monthIndexStorage.getAvailableDataYearMonth();
    }
}
