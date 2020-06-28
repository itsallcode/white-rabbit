package org.itsallcode.whiterabbit.logic.service.project;

import javax.json.bind.annotation.JsonbProperty;

public class Project
{
    @JsonbProperty("projectId")
    private String projectId;
    @JsonbProperty("label")
    private String label;
    @JsonbProperty("costCarrier")
    private String costCarrier;

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getCostCarrier()
    {
        return costCarrier;
    }

    public void setCostCarrier(String costCarrier)
    {
        this.costCarrier = costCarrier;
    }

    @Override
    public String toString()
    {
        return "Project [projectId=" + projectId + ", label=" + label + ", costCarrier=" + costCarrier + "]";
    }
}
