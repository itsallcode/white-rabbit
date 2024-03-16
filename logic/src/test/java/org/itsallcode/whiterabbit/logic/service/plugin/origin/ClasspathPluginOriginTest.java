package org.itsallcode.whiterabbit.logic.service.plugin.origin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClasspathPluginOriginTest
{
    @Mock
    private ClassLoader classLoaderMock;
    private ClasspathPluginOrigin origin;

    @BeforeEach
    void setup()
    {
        origin = new ClasspathPluginOrigin(classLoaderMock);
    }

    @Test
    void getClassLoader()
    {
        assertThat(origin.getClassLoader()).isSameAs(classLoaderMock);
    }

    @Test
    void getDescription()
    {
        assertThat(origin.getDescription()).isEqualTo("included");
    }

    @Test
    void isExternal()
    {
        assertThat(origin.isExternal()).isFalse();
    }

    @Test
    void hasToString()
    {
        assertThat(origin).hasToString("ClassPath origin");
    }
}
