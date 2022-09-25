package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;
import java.util.List;

/**
 * An activity during a {@link ProjectReportDay day} where you worked for a
 * specific time duration on a {@link Project}.
 */
public interface ProjectReportActivity
{
    /**
     * Get the {@link Project} of this activity.
     * 
     * @return the {@link Project} of this activity.
     */
    Project getProject();

    /**
     * Get the total duration of this activity.
     * 
     * @return the total duration of this activity.
     */
    Duration getWorkingTime();

    /**
     * Get the comments for this activity.
     * 
     * @return the comments for this activity.
     */
    List<String> getComments();
}