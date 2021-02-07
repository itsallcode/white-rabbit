package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;

/**
 * Model for a project activity on a given {@link DayData day} including project
 * id, duration and comment.
 */
public interface ActivityData
{
    String getProjectId();

    void setProjectId(String id);

    Duration getDuration();

    boolean isRemainder();

    void setDuration(Duration duration);

    String getComment();

    void setComment(String comment);
}