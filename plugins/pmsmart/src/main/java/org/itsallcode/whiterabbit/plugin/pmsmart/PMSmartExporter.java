package org.itsallcode.whiterabbit.plugin.pmsmart;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverFactory;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.ProjectRow;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.WeekViewPage;

public class PMSmartExporter implements ProjectReportExporter
{
    private static final Logger LOG = LogManager.getLogger(PMSmartExporter.class);

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
        final List<ProjectReportDay> daysToExport = report.getDays().stream()
                .filter(day -> day.getType() == DayType.WORK)
                .filter(day -> !day.getProjects().isEmpty())
                .collect(toList());
        LOG.info("Exporting {} days to pmsmart...", daysToExport.size());
        progressMonitor.setTaskName("Initializing...");
        final String baseUrl = config.getMandatoryValue("baseurl");
        try (final Driver driver = webDriverFactory.createWebDriver(baseUrl))
        {
            if (progressMonitor.isCanceled())
            {
                return;
            }

            final WeekViewPage weekViewPage = driver.getWeekViewPage();

            final Map<String, ProjectRow> projects = weekViewPage.getProjectTable().getProjects();

            progressMonitor.beginTask("Initializing...", daysToExport.size());
            for (final ProjectReportDay day : daysToExport)
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
                for (final ProjectReportActivity project : day.getProjects())
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
                    projectRow.enterComment(day.getDate(), project.getComment());
                }
            }
            weekViewPage.saveWeek();
        }
    }
}
