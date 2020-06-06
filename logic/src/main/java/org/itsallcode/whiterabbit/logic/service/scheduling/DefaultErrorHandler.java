package org.itsallcode.whiterabbit.logic.service.scheduling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultErrorHandler implements ErrorHandler
{
    private static final Logger LOG = LogManager.getLogger(DefaultErrorHandler.class);

    @Override
    public void handleError(Throwable t)
    {
        LOG.error("Error occurred: {}", t.getMessage(), t);
    }
}
