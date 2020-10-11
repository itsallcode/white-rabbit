package org.itsallcode.whiterabbit.logic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.bind.JsonbBuilder;

import org.itsallcode.whiterabbit.logic.service.project.ProjectConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ClearSystemProperty(key = "user.home")
class ConfigLoaderTest
{
    @Mock
    private WorkingDirProvider workingDirProviderMock;

    @TempDir
    Path tempDir;

    private ConfigLoader loader;

    @BeforeEach
    void setUp()
    {
        loader = new ConfigLoader(workingDirProviderMock);
    }

    @Test
    void loadFromWorkingDir() throws IOException
    {
        Files.writeString(tempDir.resolve("time.properties"), "data = testing-data-dir");
        when(workingDirProviderMock.getWorkingDir()).thenReturn(tempDir);

        assertThat(loader.loadConfigFromDefaultLocations().getDataDir())
                .isEqualTo(Paths.get("testing-data-dir").toAbsolutePath());
    }

    @Test
    void loadDefaultLocation() throws IOException
    {
        Files.writeString(tempDir.resolve(".whiterabbit.properties"), "data = testing-data-dir");
        when(workingDirProviderMock.getWorkingDir()).thenReturn(tempDir.resolve("working-dir"));

        System.setProperty("user.home", tempDir.toString());

        assertThat(loader.loadConfigFromDefaultLocations().getDataDir())
                .isEqualTo(Paths.get("testing-data-dir").toAbsolutePath());
    }

    @Test
    void createsDefaultConfig() throws IOException
    {
        when(workingDirProviderMock.getWorkingDir()).thenReturn(tempDir.resolve("working-dir"));

        System.setProperty("user.home", tempDir.toString());

        final Config config = loader.loadConfigFromDefaultLocations();
        assertThat(config.getDataDir()).isEqualTo(tempDir.resolve("whiterabbit-data").toAbsolutePath());
        assertThat(config.getLocale()).hasToString("de");
        assertThat(config.getCurrentHoursPerDay()).isEmpty();

        assertThat(config.getDataDir()).exists();
        assertThat(config.getDataDir()).isDirectoryContaining(p -> p.getFileName().toString().equals("projects.json"));

        final String projectsJson = Files.readString(config.getDataDir().resolve("projects.json"));
        final ProjectConfig projectConfig = JsonbBuilder.create().fromJson(projectsJson, ProjectConfig.class);
        assertThat(projectConfig.getProjects())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("label", "My project");
    }
}
