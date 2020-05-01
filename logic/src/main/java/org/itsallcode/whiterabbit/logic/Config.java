package org.itsallcode.whiterabbit.logic;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

public interface Config
{

    public static Config read(Path configFile)
    {
        return ConfigFile.read(configFile);
    }

    Path getDataDir();

    Locale getLocale();

    Optional<Duration> getCurrentHoursPerDay();
}
