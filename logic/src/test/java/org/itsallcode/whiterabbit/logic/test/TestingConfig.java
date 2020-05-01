package org.itsallcode.whiterabbit.logic.test;

import java.nio.file.Path;
import java.util.Locale;

import org.itsallcode.whiterabbit.logic.Config;

public class TestingConfig implements Config
{
    private final Path dataDir;
    private final Locale locale;

    private TestingConfig(Builder builder)
    {
        this.dataDir = builder.dataDir;
        this.locale = builder.locale;
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

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Path dataDir;
        private Locale locale = Locale.ENGLISH;

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

        public TestingConfig build()
        {
            return new TestingConfig(this);
        }
    }
}
