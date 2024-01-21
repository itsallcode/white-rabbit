package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

class DelegatingAppServiceCallback implements AppServiceCallback
{
    private static final Logger LOG = LogManager.getLogger(DelegatingAppServiceCallback.class);

    private AppServiceCallback delegate;

    public void setDelegate(AppServiceCallback delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public void recordUpdated(DayRecord dayRecord)
    {
        if (delegate != null)
        {
            this.delegate.recordUpdated(dayRecord);
        }
        else
        {
            throw noDelegateRegistered();
        }
    }

    @Override
    public InterruptionDetectedDecision automaticInterruptionDetected(LocalTime startOfInterruption,
            Duration interruption)
    {
        if (delegate != null)
        {
            return delegate.automaticInterruptionDetected(startOfInterruption, interruption);
        }
        else
        {
            throw noDelegateRegistered();
        }
    }

    @Override
    public void exceptionOccurred(Exception e)
    {
        if (delegate != null)
        {
            delegate.exceptionOccurred(e);
        }
        else
        {
            LOG.error("An error occurred: {}", e.getMessage(), e);
        }
    }

    @Override
    public void workStoppedForToday(boolean stopWorking)
    {
        if (delegate != null)
        {
            delegate.workStoppedForToday(stopWorking);
        }
        else
        {
            throw noDelegateRegistered();
        }
    }

    private RuntimeException noDelegateRegistered()
    {
        return new IllegalStateException("No delegate registered");
    }
}
