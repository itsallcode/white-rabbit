package org.itsallcode.whiterabbit.api.model;

import java.time.Duration;

import org.itsallcode.whiterabbit.logic.service.project.Project;

public interface IProjectReportActivity
{

    Project getProject();

    Duration getWorkingTime();

    String getComment();

}