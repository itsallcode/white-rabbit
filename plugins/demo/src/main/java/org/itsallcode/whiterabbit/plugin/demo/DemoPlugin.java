package org.itsallcode.whiterabbit.plugin.demo;

import java.util.Optional;

import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.ProjectReportExporter;
import org.itsallcode.whiterabbit.api.WhiteRabbitPlugin;

public class DemoPlugin implements WhiteRabbitPlugin
{
    @Override
    public void init(PluginConfiguration config)
    {
        // ignore
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
