package org.itsallcode.whiterabbit.logic.service.scheduling;

import static org.mockito.Mockito.verify;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoggingErrorHandlerTest
{
    @Mock
    private Logger loggerMock;
    private LoggingErrorHandler loggingErrorHandler;

    @BeforeEach
    void setUp()
    {
        loggingErrorHandler = new LoggingErrorHandler(loggerMock);
    }

    @Test
    void test()
    {
        final RuntimeException exception = new RuntimeException("msg");
        loggingErrorHandler.handleError(exception);
        verify(loggerMock).error("Error occurred: {}", "msg", exception);
    }
}
