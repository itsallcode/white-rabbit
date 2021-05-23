package org.itsallcode.whiterabbit.plugin.pmsmart.integration;

import java.nio.file.Path;
import java.time.YearMonth;
import java.util.Optional;

import javax.json.bind.Jsonb;

import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.logic.storage.data.JsonFileStorage;
import org.itsallcode.whiterabbit.logic.storage.data.JsonModelFactory;
import org.itsallcode.whiterabbit.logic.storage.data.JsonbFactory;

public class JsonFileStorageMock extends JsonFileStorage
{
    public static JsonFileStorageMock create(Path timeRecordingFile)
    {
        final Jsonb jsonb = new JsonbFactory().create();
        return new JsonFileStorageMock(jsonb, timeRecordingFile);
    }

    private final Path timeRecordingFile;

    private JsonFileStorageMock(Jsonb jsonb, Path timeRecordingFile)
    {
        super(jsonb, null, new JsonModelFactory());
        this.timeRecordingFile = timeRecordingFile;
    }

    @Override
    public Optional<MonthData> load(YearMonth date)
    {
        return Optional.of(super.loadFromFile(timeRecordingFile));
    }

}
