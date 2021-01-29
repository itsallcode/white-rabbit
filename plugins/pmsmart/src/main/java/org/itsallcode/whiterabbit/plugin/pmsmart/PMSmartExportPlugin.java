package org.itsallcode.whiterabbit.plugin.pmsmart;

import java.util.Optional;

import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverFactory;

public class PMSmartExportPlugin implements Plugin
{
    private PluginConfiguration config;

    @Override
    public void init(PluginConfiguration config)
    {
        this.config = config;
    }

    @Override
    public Optional<ProjectReportExporter> projectReportExporter()
    {
        return Optional.of(new PMSmartExporter(config, new WebDriverFactory()));
    }

    @Override
    public String getId()
    {
        return "pmsmart";
    }
}
