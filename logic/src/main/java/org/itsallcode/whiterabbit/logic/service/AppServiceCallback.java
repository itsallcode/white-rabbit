package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

public interface AppServiceCallback
{
    enum InterruptionDetectedDecision
    {
        ADD_INTERRUPTION, SKIP_INTERRUPTION, STOP_WORKING_FOR_TODAY
    }

    void recordUpdated(DayRecord record);

    InterruptionDetectedDecision automaticInterruptionDetected(LocalTime startOfInterruption, Duration interruption);

    void workStoppedForToday(boolean stopWorking);

    void exceptionOccurred(Exception e);
}
