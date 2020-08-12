package org.itsallcode.whiterabbit.logic.service.project;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({})
public class ProjectConfig
{
    @JsonbProperty("projects")
    private List<Project> projects;

    public List<Project> getProjects()
    {
        return projects;
    }

    public void setProjects(List<Project> projects)
    {
        this.projects = projects;
    }
}
