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
     * @return the {@link Project} of this activity.
     */
    Project getProject();

    /**
     * @return the total duration of this activity.
     */
    Duration getWorkingTime();

    /**
     * @return the comments for this activity.
     */
    List<String> getComments();
}