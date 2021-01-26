package org.itsallcode.whiterabbit.plugin.pmsmart;

import java.util.Optional;

import org.itsallcode.whiterabbit.plugin.PluginConfiguration;
import org.itsallcode.whiterabbit.plugin.ProjectReportExporter;
import org.itsallcode.whiterabbit.plugin.WhiteRabbitPlugin;

public class PMSmartExportPlugin implements WhiteRabbitPlugin
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
        return Optional.of(new PMSmartExporter(config));
    }
}
