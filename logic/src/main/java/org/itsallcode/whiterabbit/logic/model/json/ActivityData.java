package org.itsallcode.whiterabbit.logic.model.json;

import java.time.Duration;

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