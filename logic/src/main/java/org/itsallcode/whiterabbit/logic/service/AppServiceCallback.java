package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

public interface AppServiceCallback
{
    static AppServiceCallback createOnlyUpdate(Consumer<DayRecord> recordUpdatedCallback)
    {
        final Logger log = LogManager.getLogger(AppServiceCallback.class);
        return new AppServiceCallback()
        {
            @Override
            public boolean shouldAddAutomaticInterruption(LocalTime startOfInterruption, Duration interruption)
            {
                return true;
            }

            @Override
            public void recordUpdated(DayRecord record)
            {
                recordUpdatedCallback.accept(record);
            }

            @Override
            public void exceptionOccurred(Exception e)
            {
                log.error("An error occurred: {}", e.getMessage(), e);
            }

            @Override
            public void workStoppedForToday(boolean stopWorking)
            {
                // Ignore
            }
        };
    }

    void recordUpdated(DayRecord record);

    boolean shouldAddAutomaticInterruption(LocalTime startOfInterruption, Duration interruption);

    void workStoppedForToday(boolean stopWorking);

    void exceptionOccurred(Exception e);
}
