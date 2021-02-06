package org.itsallcode.whiterabbit.logic.storage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.itsallcode.whiterabbit.api.MonthDataStorage;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.storage.data.JsonFileStorage;

public interface CachingStorage extends Storage
{
    static CachingStorage create(Path dataDir, ContractTermsService contractTerms, ProjectService projectService)
    {
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        final MonthDataStorage fileStorage = new JsonFileStorage(jsonb, dataDir);
        final MonthIndexStorage monthIndexStorage = new MonthIndexStorage(contractTerms, projectService, fileStorage);
        return new CachingStorageImpl(monthIndexStorage);
    }

    List<DayRecord> getLatestDays(LocalDate maxAge);
}
