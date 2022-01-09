package org.itsallcode.whiterabbit.api.model;

/**
 * The project you work on during an {@link ProjectReportActivity activity}
 * containing an ID, label and cost carrier id.
 */
public interface Project
{
    /**
     * @return the ID of this project.
     */
    String getProjectId();

    /**
     * @return the label of this project.
     */
    String getLabel();

    /**
     * @return the cost carrier of this project.
     */
    String getCostCarrier();
}