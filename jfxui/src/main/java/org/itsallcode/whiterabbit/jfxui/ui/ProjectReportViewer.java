package org.itsallcode.whiterabbit.jfxui.ui;

import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;

import javafx.stage.Stage;

public class ProjectReportViewer
{
    private final ProjectReport report;
    private final Stage stage;

    public ProjectReportViewer(Stage stage, ProjectReport report)
    {
        this.stage = stage;
        this.report = report;
    }

    public void show()
    {

    }
}
