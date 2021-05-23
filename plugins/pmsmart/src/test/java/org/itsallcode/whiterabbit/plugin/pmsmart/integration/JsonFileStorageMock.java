package org.itsallcode.whiterabbit.plugin.pmsmart.integration;

import java.nio.file.Path;
import java.time.YearMonth;
import java.util.Optional;

import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.logic.storage.data.JsonFileStorage;
import org.itsallcode.whiterabbit.logic.storage.data.JsonModelFactory;
import org.itsallcode.whiterabbit.logic.storage.data.JsonbFactory;

public class JsonFileStorageMock extends JsonFileStorage
{
    private final Path timeRecordingFile;

    public JsonFileStorageMock(Path timeRecordingFile)
    {
        super(new JsonbFactory().create(), null, new JsonModelFactory());
        this.timeRecordingFile = timeRecordingFile;
    }

    @Override
    public Optional<MonthData> load(YearMonth date)
    {
        return Optional.of(super.loadFromFile(timeRecordingFile));
    }

}
