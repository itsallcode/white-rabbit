package org.itsallcode.whiterabbit.jfxui.property;

import org.itsallcode.whiterabbit.logic.service.scheduling.ScheduledTaskFuture;
import org.itsallcode.whiterabbit.logic.service.scheduling.Trigger;

@FunctionalInterface
public interface SchedulingService
{
    ScheduledTaskFuture schedule(Trigger trigger, Runnable command);
}
