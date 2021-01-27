package org.itsallcode.whiterabbit.plugin.pmsmart;

import java.util.Map;

import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;
import org.itsallcode.whiterabbit.plugin.PluginConfiguration;
import org.itsallcode.whiterabbit.plugin.ProgressMonitor;
import org.itsallcode.whiterabbit.plugin.ProjectReportExporter;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverFactory;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.ProjectRow;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.WeekViewPage;

public class PMSmartExporter implements ProjectReportExporter
{
    private final PluginConfiguration config;

    public PMSmartExporter(PluginConfiguration config)
    {
        this.config = config;
    }

    @Override
    public void export(ProjectReport report, ProgressMonitor progressMonitor)
    {
        progressMonitor.setTaskName("Initializing...");
        try (final Driver driver = new WebDriverFactory().createWebDriver())
        {
            if (progressMonitor.isCanceled())
            {
                return;
            }
            final String baseUrl = config.getMandatoryValue("pmsmart.baseurl");
            driver.get(baseUrl + "/Pages/TimeTracking/TimeBookingWeek.aspx");
            final WeekViewPage weekViewPage = new WeekViewPage(driver);
            weekViewPage.assertOnPage();
            final Map<String, ProjectRow> projects = weekViewPage.getProjectTable().getProjects();

            progressMonitor.beginTask("Initializing...", report.days.size());
            for (final Day day : report.days)
            {
                if (progressMonitor.isCanceled())
                {
                    return;
                }
                progressMonitor.worked(1);
                progressMonitor.setTaskName("Exporting day " + day.date + "...");
                if (!weekViewPage.isDaySelected(day.date))
                {
                    weekViewPage.saveWeek();
                    weekViewPage.selectWeek(day.date);
                }
                for (final ProjectActivity project : day.projects)
                {
                    if (progressMonitor.isCanceled())
                    {
                        return;
                    }
                    final String costCarrier = project.getProject().getCostCarrier();
                    final ProjectRow projectRow = projects.get(costCarrier);
                    if (projectRow == null)
                    {
                        throw new IllegalStateException("Project '" + costCarrier + "' not found as favorite");
                    }
                    projectRow.enterDuration(day.date, project.getWorkingTime());
                }
            }
            weekViewPage.saveWeek();
        }
    }
}
