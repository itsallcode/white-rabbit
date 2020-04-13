package org.itsallcode.whiterabbit.logic.service.singleinstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;

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
        final SingleInstanceService service = new SingleInstanceService(9999999, null);
        assertThatThrownBy(() -> service.tryToRegisterInstance(callbackMock))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("port out of range:9999999");
    }

    @Test
    void registerSuccessfulWhenNoOtherServiceRunning()
    {
        final SingleInstanceService service = create();
        assertThat(service.tryToRegisterInstance(callbackMock)).isNotPresent();
        service.close();
    }

    @Test
    void registerReturnsOtherInstanceWhenNoOtherServiceRunning()
    {
        final SingleInstanceService first = create();
        assertThat(first.tryToRegisterInstance(callbackMock)).isNotPresent();

        final SingleInstanceService second = create();
        assertThat(second.tryToRegisterInstance(callbackMock)).isPresent();

        first.close();
    }

    @Test
    void registerPossibleWhenOtherServiceClosed()
    {
        final SingleInstanceService first = create();
        SingleInstanceService second;
        try
        {
            assertThat(first.tryToRegisterInstance(callbackMock)).isNotPresent();
            second = create();
            assertThat(second.tryToRegisterInstance(callbackMock)).isPresent();
        }
        finally
        {
            first.close();
        }
        try
        {
            assertThat(second.tryToRegisterInstance(callbackMock)).isNotPresent();
        }
        finally
        {
            second.close();
        }
    }

    @Test
    void sendMessageToRunningInstance() throws IOException
    {
        final SingleInstanceService first = create();
        try
        {
            assertThat(first.tryToRegisterInstance(callbackMock)).isNotPresent();

            final SingleInstanceService second = create();
            final Optional<OtherInstance> remote = second.tryToRegisterInstance(null);
            assertThat(remote).isPresent();
            remote.get().sendMessage("msg");
            remote.get().close();

            verify(callbackMock).messageReceived("msg");
        }
        finally
        {
            first.close();
        }
    }

    private SingleInstanceService create()
    {
        return new SingleInstanceService(PORT, Executors.newFixedThreadPool(1));
    }
}
