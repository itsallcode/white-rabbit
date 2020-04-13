package org.itsallcode.whiterabbit.logic.service.singleinstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SingleInstanceServiceTest
{
    private static final int PORT = 34567;

    @Mock
    private RunningInstanceCallback callbackMock;

    @Test
    void registerInvalidPortFails()
    {
        final SingleInstanceService service = new SingleInstanceService(9999999);
        assertThatThrownBy(() -> service.tryToRegisterInstance(callbackMock))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("port out of range:9999999");
    }

    @Test
    void registerSuccessfulWhenNoOtherServiceRunning() throws IOException
    {
        final SingleInstanceService service = create();
        try (var result = service.tryToRegisterInstance(callbackMock))
        {
            assertThat(result.isOtherInstanceRunning()).isFalse();
        }
    }

    @Test
    void registerReturnsOtherInstanceWhenNoOtherServiceRunning() throws IOException
    {
        final SingleInstanceService service = create();
        try (RegistrationResult firstResult = service.tryToRegisterInstance(callbackMock))
        {
            assertThat(firstResult.isOtherInstanceRunning()).isFalse();

            try (var second = service.tryToRegisterInstance(callbackMock))
            {
                assertThat(second.isOtherInstanceRunning()).isTrue();
            }
        }
    }

    @Test
    void registerPossibleWhenOtherServiceClosed() throws IOException
    {
        final SingleInstanceService service = create();
        try (final RegistrationResult firstResult = service.tryToRegisterInstance(callbackMock))
        {
            assertThat(firstResult.isOtherInstanceRunning()).isFalse();
            try (var secondResult = service.tryToRegisterInstance(callbackMock))
            {
                assertThat(secondResult.isOtherInstanceRunning()).isTrue();
            }
        }
        try (RegistrationResult secondResult = service.tryToRegisterInstance(null))
        {
            assertThat(secondResult.isOtherInstanceRunning()).isFalse();
        }
    }

    @Test
    void sendMessageToRunningInstance() throws IOException, InterruptedException
    {
        final SingleInstanceService service = create();
        try (var first = service.tryToRegisterInstance(callbackMock))
        {
            assertThat(first.isOtherInstanceRunning()).isFalse();
            try (var remote = service.tryToRegisterInstance(null))
            {
                assertThat(remote.isOtherInstanceRunning()).isTrue();
                remote.sendMessage("msg");
            }
            Thread.sleep(500);
        }
        verify(callbackMock, timeout(50).atLeastOnce()).messageReceived("msg");
    }

    private SingleInstanceService create()
    {
        return new SingleInstanceService(PORT);
    }
}
