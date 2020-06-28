package org.itsallcode.whiterabbit.logic.service.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.itsallcode.whiterabbit.logic.test.TestingConfig;
import org.junit.jupiter.api.Test;

class ProjectServiceTest
{
    @Test
    void doesNotFailWhenConfigFileMissing()
    {
        final Collection<Project> availableProjects = create(Paths.get("no-such-dir"))
                .getAvailableProjects();
        assertThat(availableProjects).hasSize(0);
    }

    @Test
    void loadsProjects()
    {
        final Collection<Project> availableProjects = create(Paths.get("src/test/resources/projects"))
                .getAvailableProjects();
        assertThat(availableProjects).hasSize(4)
                .extracting(Project::getProjectId).containsExactly("p1", "p2", "general", "training");
    }

    private ProjectService create(Path dataDir)
    {
        return new ProjectService(TestingConfig.builder().withDataDir(dataDir).build());
    }
}
