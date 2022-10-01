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
    private final Duration mandatoryBreak;

    private TestingConfig(final Builder builder)
    {
        this.dataDir = builder.dataDir;
        this.locale = builder.locale;
        this.currentHoursPerDay = builder.currentHoursPerDay;
        this.mandatoryBreak = builder.mandatoryBreak;
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
    public Optional<Duration> getMandatoryBreak()
    {
        return Optional.ofNullable(mandatoryBreak);
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
    public String getMandatoryValue(final String key)
    {
        return null;
    }

    @Override
    public Path getUserDir()
    {
        return null;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        public Duration mandatoryBreak;
        private Path dataDir;
        private Locale locale;
        private Duration currentHoursPerDay;

        private Builder()
        {
        }

        public Builder withDataDir(final Path dataDir)
        {
            this.dataDir = dataDir;
            return this;
        }

        public Builder withLocale(final Locale locale)
        {
            this.locale = locale;
            return this;
        }

        public Builder withCurrentHoursPerDay(final Duration currentHoursPerDay)
        {
            this.currentHoursPerDay = currentHoursPerDay;
            return this;
        }

        public Builder withMandatoryBreak(final Duration mandatoryBreak)
        {
            this.mandatoryBreak = mandatoryBreak;
            return this;
        }

        public TestingConfig build()
        {
            return new TestingConfig(this);
        }
    }

    @Override
    public Optional<String> getOptionalValue(final String key)
    {
        return Optional.empty();
    }
}
