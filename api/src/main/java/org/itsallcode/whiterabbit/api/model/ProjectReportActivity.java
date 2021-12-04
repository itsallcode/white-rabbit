package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;
import java.util.List;

/**
 * An activity during a {@link ProjectReportDay day} where you worked for a
 * specific time duration on a {@link Project}.
 */
public interface ProjectReportActivity
{
    Project getProject();

    Duration getWorkingTime();

    List<String> getComments();
}