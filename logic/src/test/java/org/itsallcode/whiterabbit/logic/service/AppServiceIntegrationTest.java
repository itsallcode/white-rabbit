package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Locale;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.service.singleinstance.OtherInstance;
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
        final var singleInstance = new SingleInstanceService();
        final Optional<OtherInstance> otherInstance = singleInstance.tryToRegisterInstance(null);
        singleInstance.close();
        assumeTrue(otherInstance.isEmpty(), "Another instance is already running");
    }
}
