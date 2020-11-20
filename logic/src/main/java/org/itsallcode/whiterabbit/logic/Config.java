package org.itsallcode.whiterabbit.logic;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

public interface Config
{
    Path getDataDir();

    Locale getLocale();

    Optional<Duration> getCurrentHoursPerDay();

    boolean allowMultipleInstances();

    default Path getProjectFile()
    {
        return getDataDir().resolve("projects.json");
    }

    Path getConfigFile();
}
