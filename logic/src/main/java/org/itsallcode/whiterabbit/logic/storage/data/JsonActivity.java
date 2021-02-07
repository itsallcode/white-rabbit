package org.itsallcode.whiterabbit.logic.storage.data;

import java.time.Duration;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.api.model.ActivityData;
import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

@JsonbPropertyOrder({ "projectId", "duration", "comment" })
@JsonbVisibility(FieldAccessStrategy.class)
public class JsonActivity implements ActivityData
{
    @JsonbProperty("projectId")
    private String projectId;
    @JsonbProperty("duration")
    private Duration duration;
    @JsonbProperty("comment")
    private String comment;

    @Override
    public String getProjectId()
    {
        return projectId;
    }

    @Override
    public void setProjectId(String id)
    {
        this.projectId = id;
    }

    @Override
    public Duration getDuration()
    {
        return duration;
    }

    @Override
    public boolean isRemainder()
    {
        return getDuration() == null;
    }

    @Override
    public void setDuration(Duration duration)
    {
        this.duration = duration;
    }

    @Override
    public String getComment()
    {
        return comment;
    }

    @Override
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
