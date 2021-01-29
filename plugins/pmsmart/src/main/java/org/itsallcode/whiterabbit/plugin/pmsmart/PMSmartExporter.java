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
    private final WebDriverFactory webDriverFactory;

    public PMSmartExporter(PluginConfiguration config, WebDriverFactory webDriverFactory)
    {
        this.config = config;
        this.webDriverFactory = webDriverFactory;
    }

    @Override
    public void export(ProjectReport report, ProgressMonitor progressMonitor)
    {
        progressMonitor.setTaskName("Initializing...");
        final String baseUrl = config.getMandatoryValue("pmsmart.baseurl");
        try (final Driver driver = webDriverFactory.createWebDriver())
        {
            if (progressMonitor.isCanceled())
            {
                return;
            }
            driver.get(baseUrl + "/Pages/TimeTracking/TimeBookingWeek.aspx");
            final WeekViewPage weekViewPage = new WeekViewPage(driver);
            weekViewPage.assertOnPage();
            final Map<String, ProjectRow> projects = weekViewPage.getProjectTable().getProjects();

            progressMonitor.beginTask("Initializing...", report.getDays().size());
            for (final Day day : report.getDays())
            {
                if (progressMonitor.isCanceled())
                {
                    return;
                }
                progressMonitor.worked(1);
                progressMonitor.setTaskName("Exporting day " + day.getDate() + "...");
                if (!weekViewPage.isDaySelected(day.getDate()))
                {
                    weekViewPage.saveWeek();
                    weekViewPage.selectWeek(day.getDate());
                }
                for (final ProjectActivity project : day.getProjects())
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
                    projectRow.enterDuration(day.getDate(), project.getWorkingTime());
                }
            }
            weekViewPage.saveWeek();
        }
    }
}
