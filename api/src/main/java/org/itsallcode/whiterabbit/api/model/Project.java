package org.itsallcode.whiterabbit.api.model;

/**
 * The project you work on during an {@link ProjectReportActivity activity}
 * containing an ID, label and cost carrier id.
 */
public interface Project
{
    String getProjectId();

    String getLabel();

    String getCostCarrier();
}