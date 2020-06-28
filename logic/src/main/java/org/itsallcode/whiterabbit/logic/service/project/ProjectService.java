package org.itsallcode.whiterabbit.logic.service.project;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;

public class ProjectService
{
    private static final Logger LOG = LogManager.getLogger(ProjectService.class);

    private final Jsonb jsonb;
    private final Config config;

    private final Map<String, Project> projectsById;
    private final Map<String, Project> projectsByLabel;

    public ProjectService(Config config)
    {
        this(JsonbBuilder.create(), config);
    }

    ProjectService(Jsonb jsonb, Config config)
    {
        this.jsonb = jsonb;
        this.config = config;
        final List<Project> allProjects = loadAvailableProjects();
        projectsById = groupBy(allProjects, Project::getProjectId);
        projectsByLabel = groupBy(allProjects, Project::getLabel);
    }

    private LinkedHashMap<String, Project> groupBy(final List<Project> allProjects,
            Function<Project, String> keyMapper)
    {
        return allProjects.stream()
                .collect(toMap(keyMapper, Function.identity(), (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Optional<Project> getProjectById(String projectId)
    {
        return Optional.ofNullable(projectsById.get(projectId));
    }

    public Optional<Project> getProjectByLabel(String label)
    {
        return Optional.ofNullable(projectsByLabel.get(label));
    }

    public Collection<Project> getAvailableProjects()
    {
        return projectsById.values();
    }

    private List<Project> loadAvailableProjects()
    {
        final Path projectConfigFile = config.getDataDir().resolve("projects.json");
        final List<Project> projectList = readProjectConfig(projectConfigFile)
                .map(ProjectConfig::getProjects)
                .orElse(emptyList());
        LOG.info("Found {} projects in file {}: {}", projectList.size(), projectConfigFile, projectList);
        return projectList;
    }

    private Optional<ProjectConfig> readProjectConfig(final Path projectConfigFile)
    {
        try (InputStream stream = Files.newInputStream(projectConfigFile))
        {
            return Optional.of(jsonb.fromJson(stream, ProjectConfig.class));
        }
        catch (final IOException e)
        {
            LOG.warn("Error reading project config from {}: {}", projectConfigFile, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
