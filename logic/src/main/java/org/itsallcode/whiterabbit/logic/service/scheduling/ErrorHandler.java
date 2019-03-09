package org.itsallcode.whiterabbit.logic.service.scheduling;

@FunctionalInterface
public interface ErrorHandler {
    void handleError(Throwable t);
}
