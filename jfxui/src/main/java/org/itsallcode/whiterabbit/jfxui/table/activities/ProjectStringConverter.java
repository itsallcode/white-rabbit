package org.itsallcode.whiterabbit.jfxui.table.activities;

import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

import javafx.util.StringConverter;

class ProjectStringConverter extends StringConverter<Project>
{
    private final ProjectService projectService;

    ProjectStringConverter(ProjectService projectService)
    {
        this.projectService = projectService;
    }

    @Override
    public String toString(Project object)
    {
        return object != null ? object.getLabel() : null;
    }

    @Override
    public Project fromString(String label)
    {
        return projectService.getProjectByLabel(label).orElse(null);
    }
}