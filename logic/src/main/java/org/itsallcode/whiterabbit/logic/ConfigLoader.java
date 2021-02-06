package org.itsallcode.whiterabbit.logic;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.service.project.ProjectConfig;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;

public class ConfigLoader
{
    private static final Logger LOG = LogManager.getLogger(ConfigLoader.class);

    private final WorkingDirProvider workingDirProvider;
    private final Jsonb jsonb;

    public ConfigLoader(WorkingDirProvider workingDirProvider)
    {
        this(workingDirProvider, JsonbBuilder.create());
    }

    private ConfigLoader(WorkingDirProvider workingDirProvider, Jsonb jsonb)
    {
        this.workingDirProvider = workingDirProvider;
        this.jsonb = jsonb;
    }

    public Config loadConfigFromDefaultLocations()
    {
        final Path configFile = findExistingConfigFile().orElseGet(this::createDefaultConfig);
        return loadConfig(configFile);
    }

    public Config loadConfig(Path location)
    {
        return ConfigFile.read(location);
    }

    private Path createDefaultConfig()
    {
        final Path path = getUserHomeConfigPath();
        final String defaultConfigContent = createDefaultConfigContent();
        LOG.info("Creating default config file {}", path);
        writeFile(path, defaultConfigContent);
        return path;
    }

    private String createDefaultConfigContent()
    {
        final Path dataDir = getUserHomePath().resolve("whiterabbit-data");
        createDir(dataDir);
        createProjectFile(dataDir);
        return "data = " + dataDir.toString().replace('\\', '/') + "\n"
                + "locale = de\n";
    }

    private void createProjectFile(Path dataDir)
    {
        final ProjectConfig projectConfig = createDefaultProjectConfig();
        final String jsonContent = jsonb.toJson(projectConfig);
        final Path projectConfigPath = dataDir.resolve("projects.json");
        LOG.info("Creating default project config file {}", projectConfigPath);
        writeFile(projectConfigPath, jsonContent);
    }

    private ProjectConfig createDefaultProjectConfig()
    {
        final ProjectConfig projectConfig = new ProjectConfig();
        final ProjectImpl project = new ProjectImpl();
        project.setLabel("My project");
        project.setCostCarrier("P100");
        project.setProjectId("mp");
        projectConfig.setProjects(List.of(project));
        return projectConfig;
    }

    private void createDir(Path dataDir)
    {
        try
        {
            Files.createDirectories(dataDir);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error creating dir " + dataDir, e);
        }
    }

    private void writeFile(final Path path, String content)
    {
        try
        {
            Files.writeString(path, content);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error writing to " + path, e);
        }
    }

    private Optional<Path> findExistingConfigFile()
    {
        final Path workingDirConfig = workingDirProvider.getWorkingDir().resolve("time.properties");
        if (Files.exists(workingDirConfig))
        {
            return Optional.of(workingDirConfig);
        }
        final Path userHomeConfig = getUserHomeConfigPath();
        if (Files.exists(userHomeConfig))
        {
            return Optional.of(userHomeConfig);
        }
        return Optional.empty();
    }

    private Path getUserHomeConfigPath()
    {
        return getUserHomePath().resolve(".whiterabbit.properties");
    }

    private Path getUserHomePath()
    {
        return Paths.get(System.getProperty("user.home"));
    }
}
