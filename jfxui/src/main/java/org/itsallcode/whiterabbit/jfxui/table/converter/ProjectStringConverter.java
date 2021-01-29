package org.itsallcode.whiterabbit.jfxui.table.converter;

import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

import javafx.util.StringConverter;

public class ProjectStringConverter extends StringConverter<ProjectImpl>
{
    private final ProjectService projectService;

    public ProjectStringConverter(ProjectService projectService)
    {
        this.projectService = projectService;
    }

    @Override
    public String toString(ProjectImpl object)
    {
        return object != null ? object.getLabel() : null;
    }

    @Override
    public ProjectImpl fromString(String label)
    {
        return projectService.getProjectByLabel(label).orElse(null);
    }
}