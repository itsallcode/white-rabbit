package org.itsallcode.whiterabbit.logic.service.plugin.origin;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JarPluginOriginTest
{
    private static final Path JAR = Paths.get("path/to/plugin.jar");

    @Mock
    private ClassLoader classLoaderMock;
    private JarPluginOrigin origin;

    @BeforeEach
    private void setup()
    {
        origin = new JarPluginOrigin(JAR, classLoaderMock);
    }

    @Test
    void getClassLoader()
    {
        assertThat(origin.getClassLoader()).isSameAs(classLoaderMock);
    }

    @Test
    void getDescription()
    {
        assertThat(origin.getDescription()).isEqualTo(JAR.toString());
    }

    @Test
    void isExternal()
    {
        assertThat(origin.isExternal()).isTrue();
    }

    @Test
    void hasToString()
    {
        assertThat(origin).hasToString("Jar origin [" + JAR + "]");
    }
}
