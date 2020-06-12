package org.itsallcode.whiterabbit.jfxui.activities;

import org.itsallcode.whiterabbit.logic.model.Activity;

@FunctionalInterface
public interface ActivityEditListener
{
    void recordUpdated(Activity record);
}
