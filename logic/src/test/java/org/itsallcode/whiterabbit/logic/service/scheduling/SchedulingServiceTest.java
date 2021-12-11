package org.itsallcode.whiterabbit.logic.service.scheduling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceTest
{
    private static final Instant NOW = Instant.parse("2007-12-03T10:15:30.00Z");

    @Mock
    private ClockService clockServiceMock;
    @Mock
    private ScheduledExecutorService executorServiceMock;
    @Mock
    private Runnable runnableMock;
    @Mock
    private Trigger triggerMock;

    private SchedulingService service;

    @BeforeEach
    void setUp()
    {
        service = new SchedulingService(clockServiceMock, executorServiceMock);
    }

    @Test
    void scheduleWithDelay()
    {
        service.schedule(Duration.ofMillis(150), runnableMock);
        verify(executorServiceMock).schedule(any(ErrorHandlingRunnable.class), eq(150L),
                eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void scheduleWithTrigger()
    {
        when(clockServiceMock.instant()).thenReturn(NOW);
        final Instant nextExecution = NOW.plusMillis(100);
        when(triggerMock.nextExecutionTime(any(), any())).thenReturn(nextExecution);
        service.schedule(triggerMock, runnableMock);
        verify(executorServiceMock).schedule(any(ReschedulingRunnable.class), eq(100L), eq(TimeUnit.MILLISECONDS));
    }
}
