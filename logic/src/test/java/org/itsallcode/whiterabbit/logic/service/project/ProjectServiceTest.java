package org.itsallcode.whiterabbit.logic.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.itsallcode.whiterabbit.logic.test.TestingConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProjectServiceTest
{
    @Test
    void doesNotFailWhenConfigFileMissing()
    {
        final Collection<ProjectImpl> availableProjects = create(Paths.get("no-such-dir"))
                .getAvailableProjects();
        assertThat(availableProjects).isEmpty();
    }

    @Test
    void loadsProjects()
    {
        final Collection<ProjectImpl> availableProjects = create(Paths.get("src/test/resources/projects"))
                .getAvailableProjects();
        assertThat(availableProjects).hasSize(4)
                .extracting(ProjectImpl::getProjectId).containsExactly("p1", "p2", "general", "training");
    }

    @Test
    void loadingFileWithEmptyProjectListFails(@TempDir Path tempDir) throws IOException
    {
        writeProjectsFile(tempDir, "{ \"projects\": [] }");

        assertThatThrownBy(() -> create(tempDir)).hasMessageContaining("No projects found in file");
    }

    @Test
    void loadingFileWithNullProjectListFails(@TempDir Path tempDir) throws IOException
    {
        writeProjectsFile(tempDir, "{ }");

        assertThatThrownBy(() -> create(tempDir)).hasMessageContaining("No projects found in file");
    }

    @Test
    void loadingFileWithMissingProjectIdFails(@TempDir Path tempDir) throws IOException
    {
        writeProjectsFile(tempDir, "{ \"projects\": [{\"label\": \"p\"}] }");

        assertThatThrownBy(() -> create(tempDir)).hasMessageContaining("Project ID is required for Project");
    }

    @Test
    void loadingFileWithMissingLabelFails(@TempDir Path tempDir) throws IOException
    {
        writeProjectsFile(tempDir, "{ \"projects\": [{\"projectId\": \"p\"}] }");

        assertThatThrownBy(() -> create(tempDir)).hasMessageContaining("Label is required for Project");
    }

    private void writeProjectsFile(Path tempDir, final String content) throws IOException
    {
        Files.write(tempDir.resolve("projects.json"), content.getBytes(StandardCharsets.UTF_8));
    }

    private ProjectService create(Path dataDir)
    {
        return ProjectService.load(TestingConfig.builder().withDataDir(dataDir).build().getProjectFile());
    }
}
