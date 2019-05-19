package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Consumer;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

public interface AppServiceCallback
{
    public static AppServiceCallback createOnlyUpdate(Consumer<DayRecord> recordUpdatedCallback)
    {
        return new AppServiceCallback()
        {
            @Override
            public boolean shouldAddAutomaticInterruption(LocalTime startOfInterruption,
                    Duration interruption)
            {
                return true;
            }

            @Override
            public void recordUpdated(DayRecord record)
            {
                recordUpdatedCallback.accept(record);
            }
        };
    }

    void recordUpdated(DayRecord record);

    boolean shouldAddAutomaticInterruption(LocalTime startOfInterruption, Duration interruption);
}
