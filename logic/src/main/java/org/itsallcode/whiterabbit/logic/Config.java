package org.itsallcode.whiterabbit.logic;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.service.contract.HoursPerDayProvider;
import org.itsallcode.whiterabbit.logic.service.project.ProjectFileProvider;

public interface Config
{
    static final String PROJECTS_JSON = "projects.json";

    Path getDataDir();

    Locale getLocale();

    boolean allowMultipleInstances();

    Path getConfigFile();

    HoursPerDayProvider getHoursPerDayProvider();

    // convenience
    default Optional<Duration> getCurrentHoursPerDay()
    {
        return getHoursPerDayProvider().getHoursPerDay();
    }

    default ProjectFileProvider getProjectFileProvider()
    {
        return new ProjectFileProvider()
        {
            @Override
            public Path getProjectFile()
            {
                return getDataDir().resolve(PROJECTS_JSON);
            }
        };
    }

    Path getUserDir();

    default Path getLogDir()
    {
        return getUserDir().resolve("logs");
    }

    default Path getPluginDir()
    {
        return getUserDir().resolve("plugins");
    }

    default Path getUiStatePath()
    {
        return getUserDir().resolve("ui-state.json");
    }

    String getMandatoryValue(String key);
}
