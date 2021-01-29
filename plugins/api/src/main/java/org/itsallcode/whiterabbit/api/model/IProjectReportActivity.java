package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;

public interface IProjectReportActivity
{
    IProject getProject();

    Duration getWorkingTime();

    String getComment();
}