package org.itsallcode.whiterabbit.api;

import java.util.Optional;

public interface Plugin
{
    void init(PluginConfiguration config);

    Optional<ProjectReportExporter> projectReportExporter();

    String getId();

    void close();
}
