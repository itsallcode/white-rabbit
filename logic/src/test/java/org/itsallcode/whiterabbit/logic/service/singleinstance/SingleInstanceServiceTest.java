package org.itsallcode.whiterabbit.logic.service.singleinstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SingleInstanceServiceTest
{
    private static final Logger LOG = LogManager.getLogger(SingleInstanceServiceTest.class);

    private static final int PORT = 34568;

    @Mock
    private RunningInstanceCallback callbackMock;

    @BeforeEach
    void setup(TestInfo testInfo)
    {
        LOG.debug("\n=== {} ===", testInfo.getDisplayName());
    }

    @Test
    void registerInvalidPortFails()
    {
        final SingleInstanceService service = new SingleInstanceService(9999999);
        assertThatThrownBy(() -> service.tryToRegisterInstance(callbackMock))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Port value out of range: 9999999");
    }

    @Test
    void registerSuccessfulWhenNoOtherServiceRunning()
    {
        final SingleInstanceService service = create();
        try (var result = service.tryToRegisterInstance(callbackMock))
        {
            assertOtherInstanceIsNotRunning(result);
        }
    }

    @Test
    void registerReturnsOtherInstanceWhenNoOtherServiceRunning()
    {
        final SingleInstanceService service = create();
        try (RegistrationResult firstResult = service.tryToRegisterInstance(callbackMock))
        {
            assertOtherInstanceIsNotRunning(firstResult);

            try (var second = service.tryToRegisterInstance(callbackMock))
            {
                assertOtherInstanceIsRunning(second);
            }
        }
    }

    @Test
    void registerPossibleWhenOtherServiceClosed()
    {
        final SingleInstanceService service = create();
        try (final RegistrationResult firstResult = service.tryToRegisterInstance(callbackMock))
        {
            assertOtherInstanceIsNotRunning(firstResult);
            try (var secondResult = service.tryToRegisterInstance(callbackMock))
            {
                assertOtherInstanceIsRunning(secondResult);
            }
        }
        waitUntilSocketClosed();
        try (RegistrationResult secondResult = service.tryToRegisterInstance(callbackMock))
        {
            assertOtherInstanceIsNotRunning(secondResult);
        }
    }

    @Test
    void sendMessageToRunningInstance()
    {
        final SingleInstanceService service = create();
        try (var first = service.tryToRegisterInstance(callbackMock))
        {
            assertThat(first.isOtherInstanceRunning()).as("other instance running").isFalse();
            try (var remote = service.tryToRegisterInstance(callbackMock))
            {
                assertOtherInstanceIsRunning(remote);
                remote.sendMessage("msg");
            }
            waitUntilSocketClosed();
        }
        verify(callbackMock, timeout(50).atLeastOnce()).messageReceived(eq("msg"), any());
    }

    @Test
    void sendingMessageWithNewlineFails()
    {
        final SingleInstanceService service = create();
        try (var first = service.tryToRegisterInstance(callbackMock))
        {
            assertThat(first.isOtherInstanceRunning()).as("other instance running").isFalse();
            try (var remote = service.tryToRegisterInstance(callbackMock))
            {
                assertOtherInstanceIsRunning(remote);
                assertThatThrownBy(() -> remote.sendMessage("msgWithNewline\n"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("Message 'msgWithNewline\n' must not contain \\n");
            }
            waitUntilSocketClosed();
        }
        verify(callbackMock, never()).messageReceived(any(), any());
    }

    @Test
    void sendMessageWithResponse()
    {
        final SingleInstanceService service = create();
        final RunningInstanceCallback echoCallback = (message, client) -> client.sendMessage("echo " + message);
        try (var first = service.tryToRegisterInstance(echoCallback))
        {
            assertThat(first.isOtherInstanceRunning()).as("other instance running").isFalse();
            try (var remote = service.tryToRegisterInstance(callbackMock))
            {
                assertOtherInstanceIsRunning(remote);
                assertThat(remote.sendMessageWithResponse("msg")).isEqualTo("echo msg");
            }
            waitUntilSocketClosed();
        }
    }

    @Test
    void responseWithNewlineFails()
    {
        final SingleInstanceService service = create();
        final RunningInstanceCallback echoCallback = (message, client) -> client.sendMessage("echo newline\n");
        try (var first = service.tryToRegisterInstance(echoCallback))
        {
            assertThat(first.isOtherInstanceRunning()).as("other instance running").isFalse();
            try (var remote = service.tryToRegisterInstance(callbackMock))
            {
                assertOtherInstanceIsRunning(remote);
                assertThat(remote.sendMessageWithResponse("msg")).isNull();
            }
            waitUntilSocketClosed();
        }
    }

    private void assertOtherInstanceIsRunning(RegistrationResult result)
    {
        LOG.debug("Assert that no other instance is running: "
                + (result.isOtherInstanceRunning() ? "success" : "failed"));
        assertThat(result.isOtherInstanceRunning()).as("other instance is running").isTrue();
    }

    private void assertOtherInstanceIsNotRunning(RegistrationResult result)
    {
        LOG.debug("Assert that no other instance is running: "
                + (result.isOtherInstanceRunning() ? "failed" : "success"));
        assertThat(result.isOtherInstanceRunning()).as("other instance is running").isFalse();
    }

    @SuppressWarnings("java:S2925") // Sleep required to make tests stable
    private void waitUntilSocketClosed()
    {
        try
        {
            Thread.sleep(100);
        }
        catch (final InterruptedException e)
        {
            // Ignore
        }
    }

    private SingleInstanceService create()
    {
        return new SingleInstanceService(PORT);
    }
}
