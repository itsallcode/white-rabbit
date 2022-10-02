package org.itsallcode.whiterabbit.logic.service.scheduling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class LoggingErrorHandler implements ErrorHandler
{
    private final Logger logger;

    LoggingErrorHandler()
    {
        this(LogManager.getLogger(LoggingErrorHandler.class));
    }

    LoggingErrorHandler(Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public void handleError(Throwable t)
    {
        logger.error("Error occurred: {}", t.getMessage(), t);
    }
}
