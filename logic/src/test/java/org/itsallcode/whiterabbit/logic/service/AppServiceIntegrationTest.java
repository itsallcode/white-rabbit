package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Locale;

import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.service.singleinstance.SingleInstanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppServiceIntegrationTest
{
    @Mock
    private Config configMock;
    private FormatterService formatterService;

    @BeforeEach
    void setUp()
    {
        formatterService = new FormatterService(Locale.ENGLISH);
    }

    @Test
    void testCreate()
    {
        assumeNoOtherInstanceIsRunning();
        try (var instance1 = createAppService())
        {
            assertThat(instance1).isNotNull();
        }
    }

    @Test
    void testCreateThrowsExceptionWhenOtherInstanceIsRunning()
    {
        assumeNoOtherInstanceIsRunning();
        try (var instance1 = createAppService())
        {
            assertThatThrownBy(() -> createAppService()).isInstanceOf(IllegalStateException.class)
                    .hasMessage("Another instance is already running");
        }
    }

    private AppService createAppService()
    {
        return AppService.create(configMock, formatterService);
    }

    private void assumeNoOtherInstanceIsRunning()
    {
        System.out.println("Check if another instance is running");
        final var singleInstance = new SingleInstanceService();
        try (var result = singleInstance.tryToRegisterInstance(null))
        {
            final boolean noOtherInstanceRunning = !result.isOtherInstanceRunning();
            System.out.println("Result: " + noOtherInstanceRunning);
            assumeTrue(noOtherInstanceRunning, "Another instance is already running");
        }
        try
        {
            Thread.sleep(100);
        }
        catch (final InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("No other instance running, continue with test");
    }
}
