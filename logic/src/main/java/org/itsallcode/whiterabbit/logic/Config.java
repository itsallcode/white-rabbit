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

    boolean allowMultipleInstances();

    Path getConfigFile();

    Optional<Duration> getCurrentHoursPerDay();

    default Path getProjectFile()
    {
        return getDataDir().resolve(PROJECTS_JSON);
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

    boolean getOptionalValue(String key, boolean defaultValue);
}
