package org.itsallcode.whiterabbit.plugin.demo;

import java.util.Optional;

import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.ProjectReportExporter;

public class DemoPlugin implements Plugin
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

    @Override
    public void close()
    {
        // ignore
    }
}