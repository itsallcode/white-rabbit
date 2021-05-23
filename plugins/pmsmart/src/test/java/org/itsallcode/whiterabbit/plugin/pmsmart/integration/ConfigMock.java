package org.itsallcode.whiterabbit.plugin.pmsmart.integration;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.Config;

public class ConfigMock implements Config
{
    final Path projectFile;

    public ConfigMock(Path projectFile)
    {
        this.projectFile = projectFile;
    }

    @Override
    public Path getProjectFile()
    {
        return projectFile;
    }

    @Override
    public Optional<Duration> getCurrentHoursPerDay()
    {
        return Optional.of(Duration.ofHours(8L));
    }

    // ------------------------------------------------------------------------

    @Override
    public Path getDataDir()
    {
        return null;
    }

    @Override
    public Locale getLocale()
    {
        return null;
    }

    @Override
    public boolean allowMultipleInstances()
    {
        return false;
    }

    @Override
    public Path getConfigFile()
    {
        return null;
    }

    @Override
    public Path getUserDir()
    {
        return null;
    }

    @Override
    public String getMandatoryValue(String key)
    {
        return null;
    }

}
