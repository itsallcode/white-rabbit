package org.itsallcode.whiterabbit.api;

import java.util.Optional;

public interface WhiteRabbitPlugin
{
    void init(PluginConfiguration config);

    Optional<ProjectReportExporter> projectReportExporter();

    String getId();
}
