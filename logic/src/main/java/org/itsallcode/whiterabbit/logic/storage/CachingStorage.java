package org.itsallcode.whiterabbit.logic.storage;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public interface CachingStorage extends Storage
{
    static CachingStorage create(Path dataDir, ContractTermsService contractTerms, ProjectService projectService)
    {
        final DateToFileMapper dateToFileMapper = new DateToFileMapper(dataDir);
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        final JsonFileStorage fileStorage = new JsonFileStorage(jsonb, dateToFileMapper);
        final MonthIndexStorage monthIndexStorage = new MonthIndexStorage(contractTerms, projectService, fileStorage);
        return new CachingStorageImpl(monthIndexStorage);
    }

    List<DayRecord> getLatestDays(LocalDate maxAge);
}
