package org.itsallcode.whiterabbit.logic.service.project;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
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

public class ProjectService
{
    private static final Logger LOG = LogManager.getLogger(ProjectService.class);

    private final Jsonb jsonb;

    private final Map<String, ProjectImpl> projectsById;
    private final Map<String, ProjectImpl> projectsByLabel;

    public ProjectService(ProjectFileProvider projectFileProvider)
    {
        this(JsonbBuilder.create(), projectFileProvider);
    }

    ProjectService(Jsonb jsonb, ProjectFileProvider projectFileProvider)
    {
        this.jsonb = jsonb;

        final Path projectConfigFile = projectFileProvider.getProjectFile();
        if (projectConfigFile != null && Files.exists(projectConfigFile))
        {
            final List<ProjectImpl> allProjects = loadAvailableProjects(projectConfigFile);
            projectsById = groupBy(allProjects, ProjectImpl::getProjectId);
            projectsByLabel = groupBy(allProjects, ProjectImpl::getLabel);
        }
        else
        {
            LOG.warn("Project config file not found at {}", projectConfigFile);
            projectsById = emptyMap();
            projectsByLabel = emptyMap();
        }
    }

    private LinkedHashMap<String, ProjectImpl> groupBy(final List<ProjectImpl> allProjects,
            Function<ProjectImpl, String> keyMapper)
    {
        return allProjects.stream()
                .collect(toMap(keyMapper, Function.identity(), (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Optional<ProjectImpl> getProjectById(String projectId)
    {
        return Optional.ofNullable(projectsById.get(projectId));
    }

    public Optional<ProjectImpl> getProjectByLabel(String label)
    {
        return Optional.ofNullable(projectsByLabel.get(label));
    }

    public Collection<ProjectImpl> getAvailableProjects()
    {
        return projectsById.values();
    }

    private List<ProjectImpl> loadAvailableProjects(Path projectConfigFile)
    {
        final List<ProjectImpl> projectList = readProjectConfig(projectConfigFile)
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
