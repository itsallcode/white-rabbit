package org.itsallcode.whiterabbit.logic.service.project;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;

public class ProjectService
{
    private static final Logger LOG = LogManager.getLogger(ProjectService.class);

    private final Map<String, ProjectImpl> projectsById;
    private final Map<String, ProjectImpl> projectsByLabel;

    public static ProjectService load(Path projectConfigFile)
    {
        if (!Files.exists(projectConfigFile))
        {
            LOG.warn("Project config file not found at {}", projectConfigFile);
            return new ProjectService(emptyMap(), emptyMap());
        }
        final List<ProjectImpl> allProjects = loadAvailableProjects(projectConfigFile);
        return new ProjectService(groupBy(allProjects, ProjectImpl::getProjectId),
                groupBy(allProjects, ProjectImpl::getLabel));
    }

    private ProjectService(Map<String, ProjectImpl> projectsById, Map<String, ProjectImpl> projectsByLabel)
    {
        this.projectsById = projectsById;
        this.projectsByLabel = projectsByLabel;
    }

    private static LinkedHashMap<String, ProjectImpl> groupBy(final List<ProjectImpl> allProjects,
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

    private static List<ProjectImpl> loadAvailableProjects(Path projectConfigFile)
    {
        final List<ProjectImpl> projectList = Optional.ofNullable(readProjectConfig(projectConfigFile).getProjects())
                .orElseGet(() -> emptyList());
        if (projectList.isEmpty())
        {
            throw new IllegalStateException("No projects found in file '" + projectConfigFile + "'");
        }
        LOG.info("Found {} projects in file {}: {}", projectList.size(), projectConfigFile, projectList);
        projectList.forEach(ProjectService::validateProject);
        return projectList;
    }

    private static void validateProject(ProjectImpl project)
    {
        if (project.getProjectId() == null)
        {
            throw new IllegalStateException("Project ID is required for " + project);
        }
        if (project.getLabel() == null)
        {
            throw new IllegalStateException("Label is required for " + project);
        }
    }

    private static ProjectConfig readProjectConfig(Path projectConfigFile)
    {
        final Jsonb jsonb = createJsonb();
        try (InputStream stream = Files.newInputStream(projectConfigFile))
        {
            return jsonb.fromJson(stream, ProjectConfig.class);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException(
                    "Error reading project config file " + projectConfigFile + ": " + e.getMessage(), e);
        }
        catch (final JsonbException e)
        {
            throw new IllegalArgumentException(
                    "Error parsing project config file " + projectConfigFile + ": " + e.getMessage(), e);
        }
    }

    private static Jsonb createJsonb()
    {
        return JsonbBuilder.create();
    }
}
