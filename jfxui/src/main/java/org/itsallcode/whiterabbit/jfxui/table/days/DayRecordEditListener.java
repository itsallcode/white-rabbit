package org.itsallcode.whiterabbit.jfxui.table.days;

import org.itsallcode.whiterabbit.jfxui.table.EditListener;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

public interface DayRecordEditListener extends EditListener<DayRecord>
{
    @Override
    void recordUpdated(DayRecord record);
}
