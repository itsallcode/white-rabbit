package org.itsallcode.whiterabbit.logic;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class ConfigTest
{
    Config config = new TestingConfig();

    @Test
    void getProjectFile()
    {
        assertThat(config.getProjectFile()).isEqualTo(Paths.get("data/projects.json"));
    }

    @Test
    void getLogPath()
    {
        assertThat(config.getLogPath()).isEqualTo(Paths.get("data/logs"));
    }

    @Test
    void getUiStatePath()
    {
        assertThat(config.getUiStatePath()).isEqualTo(Paths.get("data/ui-state.json"));
    }

    private static class TestingConfig implements Config
    {

        @Override
        public Path getDataDir()
        {
            return Paths.get("data");
        }

        @Override
        public Locale getLocale()
        {
            return null;
        }

        @Override
        public Optional<Duration> getCurrentHoursPerDay()
        {
            return null;
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
    }
}