package org.itsallcode.whiterabbit.plugin.pmsmart;

import java.time.LocalDate;
import java.util.List;

import org.itsallcode.whiterabbit.logic.report.project.ProjectReport;
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
        final String url = config.getOption("pmsmart.baseurl");
        driver.get(url + "/Pages/TimeTracking/TimeBookingWeek.aspx");
        final WeekViewPage weekViewPage = new WeekViewPage(driver);
        weekViewPage.assertOnPage();
        for (int i = 1; i <= 31; i++)
        {
            weekViewPage.selectWeek(LocalDate.of(2021, 1, i));
        }
        final List<ProjectRow> projects = weekViewPage.getProjectTable().getRows();
        System.out.println(projects);
    }
}
