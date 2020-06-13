package org.itsallcode.whiterabbit.jfxui.table.activities;

import org.itsallcode.whiterabbit.logic.model.Activity;

@FunctionalInterface
public interface ActivityEditListener
{
    void recordUpdated(int row, Activity record);
}
