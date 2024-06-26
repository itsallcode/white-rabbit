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
    void getUiStatePath()
    {
        assertThat(config.getUiStatePath()).isEqualTo(Paths.get("userDir/ui-state.json"));
    }

    @Test
    void getPluginDir()
    {
        assertThat(config.getPluginDir()).isEqualTo(Paths.get("userDir/plugins"));
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
            return Optional.empty();
        }

        @Override
        public Optional<Duration> getMandatoryBreak()
        {
            return Optional.empty();
        }

        @Override
        public boolean reduceMandatoryBreakByInterruption()
        {
            return false;
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
            return Paths.get("userDir");
        }

        @Override
        public Optional<String> getOptionalValue(final String key)
        {
            return Optional.empty();
        }
    }
}
