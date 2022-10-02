package org.itsallcode.whiterabbit.api.model;

/**
 * The project you work on during an {@link ProjectReportActivity activity}
 * containing an ID, label and cost carrier id.
 */
public interface Project
{
    /**
     * Get the ID of this project.
     * 
     * @return the ID of this project.
     */
    String getProjectId();

    /**
     * Get the label of this project.
     * 
     * @return the label of this project.
     */
    String getLabel();

    /**
     * Get the cost carrier of this project.
     * 
     * @return the cost carrier of this project.
     */
    String getCostCarrier();
}