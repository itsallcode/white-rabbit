package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;

public interface ProjectReportActivity
{
    Project getProject();

    Duration getWorkingTime();

    String getComment();
}