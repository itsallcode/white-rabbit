package org.itsallcode.whiterabbit.logic.model.json;

import java.time.Duration;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbVisibility;

@JsonbPropertyOrder({ "projectId", "duration", "comment" })
@JsonbVisibility(FieldAccessStrategy.class)
public class JsonActivity
{
    @JsonbProperty("projectId")
    private String projectId;
    @JsonbProperty("duration")
    private Duration duration;
    @JsonbProperty("comment")
    private String comment;

    public JsonActivity()
    {
        this.projectId = null;
        this.duration = null;
        this.comment = null;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String id)
    {
        this.projectId = id;
    }

    public Duration getDuration()
    {
        return duration;
    }

    public boolean isRemainder()
    {
        return getDuration() == null;
    }

    public void setDuration(Duration duration)
    {
        this.duration = duration;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public String toString()
    {
        return "JsonActivity [projectId=" + projectId + ", duration=" + duration + ", comment=" + comment + "]";
    }
}
