package org.itsallcode.whiterabbit.logic.service.project;

import org.itsallcode.whiterabbit.api.model.Project;

import jakarta.json.bind.annotation.JsonbProperty;

public class ProjectImpl implements Project
{
    @JsonbProperty("projectId")
    private String projectId;
    @JsonbProperty("label")
    private String label;
    @JsonbProperty("costCarrier")
    private String costCarrier;

    public ProjectImpl(String projectId, String label, String costCarrier)
    {
        this.projectId = projectId;
        this.label = label;
        this.costCarrier = costCarrier;
    }

    public ProjectImpl()
    {
        this(null, null, null);
    }

    @Override
    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Override
    public String getCostCarrier()
    {
        return costCarrier;
    }

    public void setCostCarrier(String costCarrier)
    {
        this.costCarrier = costCarrier;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((costCarrier == null) ? 0 : costCarrier.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ProjectImpl other = (ProjectImpl) obj;
        if (costCarrier == null)
        {
            if (other.costCarrier != null)
            {
                return false;
            }
        }
        else if (!costCarrier.equals(other.costCarrier))
        {
            return false;
        }
        if (label == null)
        {
            if (other.label != null)
            {
                return false;
            }
        }
        else if (!label.equals(other.label))
        {
            return false;
        }
        if (projectId == null)
        {
            if (other.projectId != null)
            {
                return false;
            }
        }
        else if (!projectId.equals(other.projectId))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Project [projectId=" + projectId + ", label=" + label + ", costCarrier=" + costCarrier + "]";
    }
}
