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

    boolean writeLogFile();

    Path getConfigFile();

    default Path getProjectFile()
    {
        return getDataDir().resolve(PROJECTS_JSON);
    }

    default Path getLogPath()
    {
        return getDataDir().resolve("logs");
    }

    default Path getUiStatePath()
    {
        return getDataDir().resolve("ui-state.json");
    }
}
