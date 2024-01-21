package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DelegatingAppServiceCallbackTest
{
    @Mock
    AppServiceCallback delegateMock;

    @Test
    void recordUpdatedNoDelegate()
    {
        DelegatingAppServiceCallback callback = testee();
        assertThatThrownBy(() -> callback.recordUpdated(null)).isInstanceOf(IllegalStateException.class)
                .hasMessage("No delegate registered");
    }

    @Test
    void recordUpdated()
    {
        DelegatingAppServiceCallback callback = testeeWithDelegate();
        DayRecord record = mock(DayRecord.class);
        callback.recordUpdated(record);
        verify(delegateMock).recordUpdated(same(record));
    }

    @Test
    void automaticInterruptionDetectedNoDelegate()
    {
        DelegatingAppServiceCallback callback = testee();
        assertThatThrownBy(() -> callback.automaticInterruptionDetected(null, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No delegate registered");
    }

    @Test
    void automaticInterruptionDetected()
    {
        DelegatingAppServiceCallback callback = testeeWithDelegate();
        callback.automaticInterruptionDetected(LocalTime.of(8, 30), Duration.ofMinutes(15));
        verify(delegateMock).automaticInterruptionDetected(LocalTime.of(8, 30), Duration.ofMinutes(15));
    }

    @Test
    void exceptionOccurredNoDelegateIgnoresError()
    {
        DelegatingAppServiceCallback callback = testee();
        assertDoesNotThrow(() -> callback.exceptionOccurred(new RuntimeException("mock")));
    }

    @Test
    void exceptionOccurred()
    {
        DelegatingAppServiceCallback callback = testeeWithDelegate();
        RuntimeException exception = new RuntimeException("mock");
        callback.exceptionOccurred(exception);
        verify(delegateMock).exceptionOccurred(same(exception));
    }

    @Test
    void workStoppedForTodayNoDelegate()
    {
        DelegatingAppServiceCallback callback = testee();
        assertThatThrownBy(() -> callback.workStoppedForToday(true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No delegate registered");
    }

    @Test
    void workStoppedForToday()
    {
        DelegatingAppServiceCallback callback = testeeWithDelegate();
        callback.workStoppedForToday(true);
        verify(delegateMock).workStoppedForToday(true);
    }

    DelegatingAppServiceCallback testee()
    {
        return new DelegatingAppServiceCallback();
    }

    DelegatingAppServiceCallback testeeWithDelegate()
    {
        DelegatingAppServiceCallback callback = new DelegatingAppServiceCallback();
        callback.setDelegate(delegateMock);
        return callback;
    }
}
