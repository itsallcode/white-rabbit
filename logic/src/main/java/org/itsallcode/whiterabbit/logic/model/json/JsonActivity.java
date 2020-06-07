package org.itsallcode.whiterabbit.logic.model.json;

import java.time.Duration;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "projectId", "duration", "comment" })
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

    }

    public JsonActivity(String projectId)
    {
        this.projectId = projectId;
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
}
