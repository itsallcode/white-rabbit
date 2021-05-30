package org.itsallcode.whiterabbit.plugin.pmsmart;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.api.model.ProjectReport;
import org.itsallcode.whiterabbit.api.model.ProjectReportActivity;
import org.itsallcode.whiterabbit.api.model.ProjectReportDay;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverFactory;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.ProjectRow;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.WeekViewPage;

public class PMSmartExporter implements ProjectReportExporter
{
    public static final String TRANSFER_COMMENTS = "transfer.comments";
    public static final String CLEAR_OTHER_PROJECTS = "clear_other_projects";
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
        try (final var driver = webDriverFactory.createWebDriver(baseUrl))
        {
            final var weekViewPage = driver.getWeekViewPage();
            new ExportHelper(progressMonitor, weekViewPage)
                    .withTransferComments(config.getOptionalValue(TRANSFER_COMMENTS, true))
                    .withClearOtherProjects(config.getOptionalValue(CLEAR_OTHER_PROJECTS, false))
                    .export(daysToExport);
        }
    }

    private static class ExportHelper
    {
        private final ProgressMonitor progressMonitor;
        private final WeekViewPage weekViewPage;
        private final Map<String, ProjectRow> projects;
        private boolean transferComments = true;
        private boolean clearOtherProjects = false;

        private ExportHelper(ProgressMonitor progressMonitor, WeekViewPage weekViewPage)
        {
            this.progressMonitor = progressMonitor;
            this.weekViewPage = weekViewPage;
            this.projects = weekViewPage.getProjectTable().getProjects();
        }

        public ExportHelper withTransferComments(boolean transferComments)
        {
            this.transferComments = transferComments;
            return this;
        }

        public ExportHelper withClearOtherProjects(boolean clearOtherProjects)
        {
            this.clearOtherProjects = clearOtherProjects;
            return this;
        }

        public void export(List<ProjectReportDay> daysToExport)
        {
            if (progressMonitor.isCanceled())
            {
                return;
            }

            progressMonitor.beginTask("Initializing...", daysToExport.size());
            for (final ProjectReportDay day : daysToExport)
            {
                if (progressMonitor.isCanceled())
                {
                    return;
                }
                progressMonitor.worked(1);
                progressMonitor.setTaskName("Exporting day " + day.getDate() + "...");
                selectDay(day);
                if (clearOtherProjects)
                {
                    clearOtherProjects(day);
                }
                for (final ProjectReportActivity project : day.getProjects())
                {
                    if (progressMonitor.isCanceled())
                    {
                        return;
                    }
                    exportProject(day, project);
                }
            }
            weekViewPage.saveWeek();
        }

        private void clearOtherProjects(final ProjectReportDay day)
        {
            final Iterator<Entry<String, ProjectRow>> it = projects.entrySet().iterator();
            final Set<String> set = day.getProjects().stream()
                    .map(project -> project.getProject().getCostCarrier())
                    .collect(toSet());
            while (it.hasNext() && !progressMonitor.isCanceled())
            {
                final Entry<String, ProjectRow> entry = it.next();
                if (!set.contains(entry.getKey()))
                {
                    entry.getValue().enterDuration(day.getDate(), Duration.ZERO);
                }
            }
        }

        private void selectDay(final ProjectReportDay day)
        {
            if (weekViewPage.isDaySelected(day.getDate()))
            {
                return;
            }
            weekViewPage.saveWeek();
            weekViewPage.selectWeek(day.getDate());
        }

        private void exportProject(final ProjectReportDay day, final ProjectReportActivity project)
        {
            final String costCarrier = project.getProject().getCostCarrier();
            final var projectRow = projects.get(costCarrier);
            if (projectRow == null)
            {
                throw new IllegalStateException("Project '" + costCarrier + "' not found as favorite");
            }
            projectRow.enterDuration(day.getDate(), project.getWorkingTime());
            if (transferComments)
            {
                projectRow.enterComment(day.getDate(), project.getComment());
            }
        }
    }
}
