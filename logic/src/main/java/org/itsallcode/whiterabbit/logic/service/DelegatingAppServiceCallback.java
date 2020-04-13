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
    public void recordUpdated(DayRecord record)
    {
        if (delegate != null)
        {
            this.delegate.recordUpdated(record);
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean shouldAddAutomaticInterruption(LocalTime startOfInterruption, Duration interruption)
    {
        if (delegate != null)
        {
            return delegate.shouldAddAutomaticInterruption(startOfInterruption, interruption);
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public void exceptionOccured(Exception e)
    {
        if (delegate != null)
        {
            delegate.exceptionOccured(e);
        }
        else
        {
            LOG.error("An error occured: {}", e.getMessage(), e);
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
            throw new IllegalStateException();
        }
    }

    @Override
    public void messageFromOtherInstanceReceived(String message)
    {
        if (delegate != null)
        {
            delegate.messageFromOtherInstanceReceived(message);
        }
        else
        {
            throw new IllegalStateException();
        }
    }
}
