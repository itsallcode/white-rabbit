package org.itsallcode.whiterabbit.jfxui.table;

import org.itsallcode.whiterabbit.logic.model.RowRecord;

public interface EditListener<T extends RowRecord>
{
    void recordUpdated(T record);
}
