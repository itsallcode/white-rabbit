package org.itsallcode.whiterabbit.logic.service;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

class DelegatingUpdateListener implements UpdateListener
{
    private UpdateListener delegate;

    public void setDelegate(UpdateListener delegate)
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
}
