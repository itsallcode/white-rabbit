package org.itsallcode.whiterabbit.logic.service.project;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({})
public class ProjectConfig
{
    @JsonbProperty("projects")
    private List<ProjectImpl> projects;

    public List<ProjectImpl> getProjects()
    {
        return projects;
    }

    public void setProjects(List<ProjectImpl> projects)
    {
        this.projects = projects;
    }
}
