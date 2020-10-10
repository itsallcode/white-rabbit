package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.itsallcode.whiterabbit.logic.service.AppPropertiesService.AppProperties;
import org.junit.jupiter.api.Test;

class AppPropertiesServiceTest
{
    @Test
    void load()
    {
        final AppProperties properties = new AppPropertiesService().load();
        assertThat(properties.getVersion()).isNotEmpty();
        assertThat(properties.getPlatform()).isNotEmpty();
    }

    @Test
    void loadNonExistingResourceFails()
    {
        final AppPropertiesService service = new AppPropertiesService();
        assertThatThrownBy(() -> service.load("non-existing"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Resource 'non-existing' not found in classpath");
    }
}
