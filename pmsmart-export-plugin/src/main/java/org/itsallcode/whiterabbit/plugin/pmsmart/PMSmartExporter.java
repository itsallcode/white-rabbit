package org.itsallcode.whiterabbit.plugin.pmsmart;

import java.time.Duration;
import java.util.Map;

import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.Day;
import org.itsallcode.whiterabbit.logic.report.project.ProjectReport.ProjectActivity;
import org.itsallcode.whiterabbit.plugin.PluginConfiguration;
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
    public void export(ProjectReport report)
    {
        final Driver driver = new WebDriverFactory().createWebDriver();
        final String url = config.getMandatoryValue("pmsmart.baseurl");
        driver.get(url + "/Pages/TimeTracking/TimeBookingWeek.aspx");
        final WeekViewPage weekViewPage = new WeekViewPage(driver);
        weekViewPage.assertOnPage();
        final Map<String, ProjectRow> projects = weekViewPage.getProjectTable().getProjects();

        for (final Day day : report.days)
        {
            weekViewPage.selectWeek(day.date);
            for (final ProjectActivity project : day.projects)
            {
                weekViewPage.selectWeek(day.date);
                final String costCarrier = project.getProject().getCostCarrier();
                final ProjectRow projectRow = projects.get(costCarrier);
                if (projectRow == null)
                {
                    throw new AssertionError("Project '" + costCarrier + "' not found as favorite");
                }
                final Duration workingTime = project.getWorkingTime();
                if (!workingTime.isZero())
                {
                    projectRow.enterDuration(day.date, workingTime);
                }
            }
        }
    }
}
