package org.itsallcode.whiterabbit.plugin;

import java.util.Optional;

public interface WhiteRabbitPlugin
{
    void init(PluginConfiguration config);

    Optional<ProjectReportExporter> projectReportExporter();
}
