package org.itsallcode.whiterabbit.logic.service;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

public interface UpdateListener
{
    void recordUpdated(DayRecord record);
}
