package org.itsallcode.whiterabbit.logic;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

public interface Config
{
    static final String PROJECTS_JSON = "projects.json";

    Path getDataDir();

    Locale getLocale();

    Optional<Duration> getCurrentHoursPerDay();

    boolean allowMultipleInstances();

    Path getConfigFile();

    default Path getProjectFile()
    {
        return getDataDir().resolve(PROJECTS_JSON);
    }

    Path getUserDir();

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
