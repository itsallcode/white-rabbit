package org.itsallcode.whiterabbit.logic;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

public interface Config
{
    static Config read(WorkingDirProvider workingDirProvider)
    {
        return read(workingDirProvider.getWorkingDir().resolve("time.properties"));
    }

    static Config read(Path configFile)
    {
        return ConfigFile.read(configFile);
    }

    Path getDataDir();

    Locale getLocale();

    Optional<Duration> getCurrentHoursPerDay();
}
