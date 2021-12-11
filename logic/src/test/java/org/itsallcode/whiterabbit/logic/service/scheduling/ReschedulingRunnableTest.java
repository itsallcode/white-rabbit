package org.itsallcode.whiterabbit.logic.service.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReschedulingRunnableTest
{
    private static final Instant NOW = Instant.parse("2007-12-03T10:15:30.00Z");
    private static final Instant NEXT_EXECUTION = NOW.plusSeconds(50);
    @Mock
    private Runnable commandMock;
    @Mock
    private Trigger triggerMock;
    @Mock
    private ScheduledExecutorService executorServiceMock;
    @Mock
    private ClockService clockMock;
    @Mock
    private ErrorHandler errorHandlerMock;
    @Mock
    private ScheduledFuture<?> scheduledFutureMock;

    private ReschedulingRunnable reschedulingRunnable;

    @BeforeEach
    void setUp()
    {
        reschedulingRunnable = new ReschedulingRunnable(
                new ErrorHandlingRunnable(commandMock, errorHandlerMock), triggerMock, executorServiceMock,
                clockMock);
        lenient().when(clockMock.instant()).thenReturn(NOW);
    }

    @Test
    void testScheduleDoesNothingForNullNextExecutionTime()
    {
        when(triggerMock.nextExecutionTime(NOW, Optional.empty())).thenReturn(null);
        assertThatThrownBy(() -> reschedulingRunnable.schedule()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(commandMock, executorServiceMock);
    }

    @Test
    void testScheduleSchedulesNextExecution()
    {
        when(triggerMock.nextExecutionTime(NOW, Optional.empty())).thenReturn(NEXT_EXECUTION);
        assertThat(reschedulingRunnable.schedule()).isSameAs(reschedulingRunnable);
        verify(executorServiceMock).schedule(same(reschedulingRunnable), eq(50000L), eq(TimeUnit.MILLISECONDS));
        verifyNoInteractions(commandMock);
    }

    @Test
    void testScheduleThrowsExceptionForNextExecutionInThePast()
    {
        when(triggerMock.nextExecutionTime(NOW, Optional.empty())).thenReturn(NOW.minusMillis(1));
        assertThrows(IllegalStateException.class, () -> reschedulingRunnable.schedule());
        verifyNoInteractions(commandMock);
    }

    @Test
    void testRunThrowsExceptionWhenNoExecutionScheduled()
    {
        assertThrows(NullPointerException.class, () -> reschedulingRunnable.run());

        verify(commandMock).run();
    }

    @Test
    void testRunStartsCommand()
    {
        when(triggerMock.nextExecutionTime(eq(NOW), any())).thenReturn(NEXT_EXECUTION);

        doReturn(scheduledFutureMock).when(executorServiceMock).schedule(any(Runnable.class), anyLong(), any());
        reschedulingRunnable.schedule();
        reschedulingRunnable.run();

        verify(commandMock).run();
    }

    @Test
    void testIsCancelledNotScheduledThrowsNullPointerExceptionWhenNoExecutionScheduled()
    {
        assertThrows(NullPointerException.class, () -> reschedulingRunnable.isCancelled());
    }

    @Test
    void testIsCancelled()
    {
        scheduleExecution();
        when(scheduledFutureMock.isCancelled()).thenReturn(true);

        assertTrue(reschedulingRunnable.isCancelled());
    }

    private void scheduleExecution()
    {
        when(triggerMock.nextExecutionTime(NOW, Optional.empty())).thenReturn(NEXT_EXECUTION);
        doReturn(scheduledFutureMock).when(executorServiceMock).schedule(any(Runnable.class), anyLong(), any());

        reschedulingRunnable.schedule();
    }

    @Test
    void testIsDoneThrowsNullPointerExceptionWhenNoExecutionScheduled()
    {
        scheduleExecution();
        when(scheduledFutureMock.isDone()).thenReturn(true);

        assertThat(reschedulingRunnable.isDone()).isTrue();
    }

    @Test
    void testCancelThrowsNullPointerExceptionWhenNoExecutionScheduled()
    {
        scheduleExecution();
        reschedulingRunnable.cancel();
        verify(scheduledFutureMock).cancel(false);
    }
}
