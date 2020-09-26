package org.itsallcode.whiterabbit.logic.service.singleinstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.itsallcode.whiterabbit.logic.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SingleInstanceServiceTest
{
    @Mock
    Config configMock;

    @Test
    void returnsRealImplementationWhenMultipleInstancesNotAllowed()
    {
        when(configMock.allowMultipleInstances()).thenReturn(false);

        assertThat(SingleInstanceService.create(configMock)).isInstanceOf(SingleInstanceServiceImpl.class);
    }

    @Test
    void returnsDummyImplementationWhenMultipleInstancesAllowed()
    {
        when(configMock.allowMultipleInstances()).thenReturn(true);

        assertThat(SingleInstanceService.create(configMock)).isInstanceOf(SingleInstanceServiceDummyImpl.class);
    }
}
