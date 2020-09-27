package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

public interface AppServiceCallback
{

    void recordUpdated(DayRecord record);

    boolean shouldAddAutomaticInterruption(LocalTime startOfInterruption, Duration interruption);

    void workStoppedForToday(boolean stopWorking);

    void exceptionOccurred(Exception e);
}
