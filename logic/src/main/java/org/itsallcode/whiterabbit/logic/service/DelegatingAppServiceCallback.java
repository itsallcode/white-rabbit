package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

class DelegatingAppServiceCallback implements AppServiceCallback
{
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
    }

    @Override
    public boolean shouldAddAutomaticInterruption(LocalTime startOfInterruption,
            Duration interruption)
    {
        if (delegate != null)
        {
            return delegate.shouldAddAutomaticInterruption(startOfInterruption, interruption);
        }
        else
        {
            return true;
        }
    }
}
