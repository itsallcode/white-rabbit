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

public interface Storage
{

    public static Storage create(Path dataDir, ContractTermsService contractTerms, ProjectService projectService)
    {
        final DateToFileMapper dateToFileMapper = new DateToFileMapper(dataDir);
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        final JsonFileStorage fileStorage = new JsonFileStorage(jsonb, dateToFileMapper);
        final MonthIndexStorage monthIndexStorage = new MonthIndexStorage(contractTerms, projectService, fileStorage);
        return new CachingStorage(monthIndexStorage);
    }

    Optional<MonthIndex> loadMonth(YearMonth date);

    MonthIndex loadOrCreate(YearMonth yearMonth);

    void storeMonth(MonthIndex month);

    MultiMonthIndex loadAll();

    List<YearMonth> getAvailableDataYearMonth();

}