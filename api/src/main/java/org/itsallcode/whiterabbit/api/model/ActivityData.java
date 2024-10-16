package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;

/**
 * Model for a project activity on a given {@link DayData day} including project
 * id, duration and comment.
 */
public interface ActivityData
{
    /**
     * Get the project's ID.
     * 
     * @return the project's ID.
     */
    String getProjectId();

    /**
     * Set the project's ID.
     * 
     * @param id
     *            the new ID.
     */
    void setProjectId(String id);

    /**
     * Get the duration of the activity.
     * 
     * @return the duration of the activity.
     */
    Duration getDuration();

    /**
     * Check if this is the remainder activity.
     * 
     * @return {@code true} if this is the remainder activity i.e. the duration
     *         is {@code null}.
     */
    boolean isRemainder();

    /**
     * Set the duration of the activity.
     * 
     * @param duration
     *            the new duration. Set to {@code null} for the remainder
     *            activity.
     */
    void setDuration(Duration duration);

    /**
     * Get the activity's comment.
     * 
     * @return the activity's comment.
     */
    String getComment();

    /**
     * Set the activity's comment.
     * 
     * @param comment
     *            the new comment.
     */
    void setComment(String comment);
}
