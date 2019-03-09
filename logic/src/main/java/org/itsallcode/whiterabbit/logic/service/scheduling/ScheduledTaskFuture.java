package org.itsallcode.whiterabbit.logic.service.scheduling;

public interface ScheduledTaskFuture {
    void cancel();

    boolean isDone();

    boolean isCancelled();
}
