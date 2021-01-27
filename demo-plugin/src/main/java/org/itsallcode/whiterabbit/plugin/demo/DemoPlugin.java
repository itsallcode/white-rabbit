package org.itsallcode.whiterabbit.plugin.demo;

import java.util.Optional;

import org.itsallcode.whiterabbit.plugin.PluginConfiguration;
import org.itsallcode.whiterabbit.plugin.ProjectReportExporter;
import org.itsallcode.whiterabbit.plugin.WhiteRabbitPlugin;

public class DemoPlugin implements WhiteRabbitPlugin
{
    @Override
    public void init(PluginConfiguration config)
    {
    }

    @Override
    public Optional<ProjectReportExporter> projectReportExporter()
    {
        return Optional.of(new DemoProjectReportExporter());
    }

    @Override
    public String getId()
    {
        return "demo";
    }
}
