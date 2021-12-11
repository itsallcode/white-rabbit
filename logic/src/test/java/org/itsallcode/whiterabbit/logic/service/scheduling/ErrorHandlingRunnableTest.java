package org.itsallcode.whiterabbit.logic.service.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.reflect.UndeclaredThrowableException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErrorHandlingRunnableTest
{
    @Mock
    private ErrorHandler errorHandlerMock;
    @Mock
    private Runnable runnableMock;
    private ErrorHandlingRunnable errorHandlingRunnable;

    @BeforeEach
    void setUp()
    {
        errorHandlingRunnable = new ErrorHandlingRunnable(runnableMock, errorHandlerMock);
    }

    @Test
    void runningDelegatesToRunnable()
    {
        errorHandlingRunnable.run();
        verify(runnableMock).run();
    }

    @Test
    void runningWithoutErrorDoesNotInvokeErrorHandler()
    {
        errorHandlingRunnable.run();
        verify(errorHandlerMock, never()).handleError(any());
    }

    @Test
    void throwingRuntimeExceptionInvokesErrorHandler()
    {
        final RuntimeException exception = new RuntimeException("expected");
        doThrow(exception).when(runnableMock).run();
        errorHandlingRunnable.run();
        verify(errorHandlerMock).handleError(same(exception));
    }

    @Test
    void throwingUndeclaredThrowableInvokesErrorHandler()
    {
        final RuntimeException exception = new RuntimeException("expected");
        doThrow(new UndeclaredThrowableException(exception)).when(runnableMock).run();
        errorHandlingRunnable.run();
        verify(errorHandlerMock).handleError(same(exception));
    }

    @Test
    void hasToString()
    {
        assertThat(errorHandlingRunnable).hasToString("ErrorHandlingRunnable for " + runnableMock.toString());
    }
}
