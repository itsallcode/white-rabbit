package org.itsallcode.whiterabbit.jfxui.table.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectStringConverterTest
{
    @Mock
    private ProjectService projectServiceMock;
    private ProjectStringConverter converter;

    @BeforeEach
    void setUp()
    {
        converter = new ProjectStringConverter(projectServiceMock);
    }

    @Test
    void projectNotFoundReturnsNull()
    {
        when(projectServiceMock.getProjectByLabel("project")).thenReturn(Optional.empty());
        assertThat(converter.fromString("project")).isNull();
    }

    @Test
    void projectFoundReturnsProject()
    {
        final ProjectImpl project = new ProjectImpl();
        when(projectServiceMock.getProjectByLabel("project")).thenReturn(Optional.of(project));
        assertThat(converter.fromString("project")).isSameAs(project);
    }
}
