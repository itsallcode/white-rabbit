package org.itsallcode.whiterabbit.logic.test;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.Config;

public class TestingConfig implements Config
{
    private final Path dataDir;
    private final Locale locale;
    private final Duration currentHoursPerDay;

    private TestingConfig(Builder builder)
    {
        this.dataDir = builder.dataDir;
        this.locale = builder.locale;
        this.currentHoursPerDay = builder.currentHoursPerDay;
    }

    @Override
    public Path getDataDir()
    {
        return dataDir;
    }

    @Override
    public Locale getLocale()
    {
        return locale;
    }

    @Override
    public Optional<Duration> getCurrentHoursPerDay()
    {
        return Optional.ofNullable(currentHoursPerDay);
    }

    @Override
    public boolean allowMultipleInstances()
    {
        return false;
    }

    @Override
    public boolean writeLogFile()
    {
        return false;
    }

    @Override
    public Path getConfigFile()
    {
        return null;
    }

    @Override
    public String getMandatoryValue(String key)
    {
        return null;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Path dataDir;
        private Locale locale;
        private Duration currentHoursPerDay;

        private Builder()
        {
        }

        public Builder withDataDir(Path dataDir)
        {
            this.dataDir = dataDir;
            return this;
        }

        public Builder withLocale(Locale locale)
        {
            this.locale = locale;
            return this;
        }

        public Builder withCurrentHoursPerDay(Duration currentHoursPerDay)
        {
            this.currentHoursPerDay = currentHoursPerDay;
            return this;
        }

        public TestingConfig build()
        {
            return new TestingConfig(this);
        }
    }
}
